#!/usr/bin/env python3
import re
import json
import sys
from pathlib import Path

if len(sys.argv) < 2:
    print("Usage: python repair_json_tokens.py <input.json> [output.json]")
    sys.exit(1)

inp = Path(sys.argv[1])
out = Path(sys.argv[2]) if len(sys.argv) > 2 else inp.with_name(inp.stem + ".fixed.json")

text = inp.read_text(encoding="utf-8")

# Token regex for invalid JSON tokens
token_re = re.compile(r'\b(NaN|nan|Infinity|-Infinity|undefined)\b')

def replace_outside_quotes(s):
    out_chars = []
    i = 0
    in_str = False
    esc = False
    L = len(s)
    while i < L:
        ch = s[i]
        if ch == '"' and not esc:
            in_str = not in_str
            out_chars.append(ch)
            i += 1
            continue
        if ch == '\\' and not esc:
            esc = True
            out_chars.append(ch)
            i += 1
            continue
        if esc:
            esc = False
            out_chars.append(ch)
            i += 1
            continue
        if not in_str:
            m = token_re.match(s, i)
            if m:
                out_chars.append('null')
                i = m.end()
                continue
        out_chars.append(ch)
        i += 1
    return ''.join(out_chars)

repaired = replace_outside_quotes(text)

# Try to parse JSON
try:
    data = json.loads(repaired)
except json.JSONDecodeError as e:
    print("JSON parse failed after token replacement:", e)
    out.write_text(repaired, encoding="utf-8")
    print("Wrote partially repaired file to", out)
    sys.exit(2)

# If top-level is a list, wrap into meta + records to match resources.json style
if isinstance(data, list):
    wrapped = {"meta": {"source": "imported", "retrieved": None, "version": "fixed"}, "records": data}
    final = wrapped
else:
    final = data

out.write_text(json.dumps(final, indent=2, ensure_ascii=False), encoding="utf-8")
print("Repaired JSON written to", out)
