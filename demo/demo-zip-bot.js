#!/usr/bin/env node
/**
 * demo-zip-bot.js
 *
 * Pre-scripted two-party demo for the CompassChat "Admin Team" DM.
 * Logs in as BOTH Demo Resident and Zip Carter, then fires one
 * scripted line per Enter press, alternating between the two users.
 *
 * USAGE
 *   1. Start the backend:  cd backend && mvn spring-boot:run
 *   2. Start the frontend: cd frontend && npm run dev
 *   3. Open the browser at http://localhost:5173  (auto-logs in as Demo Resident)
 *   4. Open the chat panel and click the "Admin Team" DM
 *   5. In a terminal, run:  node demo/demo-zip-bot.js
 *   6. Press Enter to fire each line, in order. Odd lines are Demo
 *      Resident, even lines are Zip — they appear live in the browser.
 *
 * REQUIREMENTS
 *   Node 18+ (uses built-in fetch)
 *   Backend running on localhost:8081
 *   "demo-resident" and "zip" users seeded with password "demo123"
 *   A DIRECT channel "dm-resident-zip" that both users are members of
 *
 * No npm install needed. Run this file directly from the repo root.
 */

import readline from 'readline';

// ----- CONFIG -----

const BACKEND = 'http://localhost:8081';

const RESIDENT = { username: 'demo-resident', password: 'demo123' };
const ZIP      = { username: 'zip',           password: 'demo123' };

/** The DIRECT channel both users share (shown as "Admin Team" in the UI). */
const CHANNEL_NAME = 'dm-resident-zip';

/** The full conversation, in order. One line per Enter press.
 *  `sender` selects which user's token posts the line. */
const SCRIPT = [
  { sender: 'resident', text: "Hi - I need a place to stay." },
  { sender: 'zip',      text: "Hi! I just saw your message - If you are between 18 and 25 you may qualify for services through FuturePath." },
  { sender: 'resident', text: "Actually, I have a housing voucher." },
  { sender: 'zip',      text: "Good news, HomeMatch offers housing search for people using a housing voucher. If you would like to learn more about either one of these or other resources, let me know and I can connect you." },
  { sender: 'resident', text: "Ok, I saw that on your website. Let me read about it and I'll get back to you." },
  { sender: 'zip',      text: "Sounds great - take your time and reach out whenever you're ready." },
];

// ----- HELPERS -----

async function login(creds) {
  const res = await fetch(`${BACKEND}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(creds),
  });
  if (!res.ok) {
    throw new Error(`Login failed for "${creds.username}" (${res.status}). Is it seeded with password "demo123"?`);
  }
  const body = await res.json();
  return body.data.token;
}

/** Find the channel id by walking the user's memberships. */
async function findChannelId(token) {
  const memberships = await get(`${BACKEND}/api/channels/mine`, token);
  for (const m of memberships.data) {
    const channel = await get(`${BACKEND}/api/channels/${m.channelId}`, token);
    if (channel.data.name === CHANNEL_NAME) {
      return channel.data.id;
    }
  }
  throw new Error(
    `Channel "${CHANNEL_NAME}" not found among the user's memberships. ` +
    `Check that the seeder created it and added both users as members.`
  );
}

async function get(url, token) {
  const res = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
  if (!res.ok) throw new Error(`GET ${url} -> ${res.status}`);
  return res.json();
}

async function sendMessage(token, channelId, content) {
  const res = await fetch(`${BACKEND}/api/channels/${channelId}/messages`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ content }),
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`Send failed (${res.status}): ${text}`);
  }
}

function label(sender) {
  return sender === 'zip' ? 'Zip Carter' : 'Demo Resident';
}

function preview(text) {
  return text.length > 60 ? text.slice(0, 57) + '...' : text;
}

// ----- SCRIPT RUNNER -----

async function main() {
  console.log('Connecting...');
  const residentToken = await login(RESIDENT);
  console.log('  Logged in as Demo Resident.');
  const zipToken = await login(ZIP);
  console.log('  Logged in as Zip.');

  const tokens = { resident: residentToken, zip: zipToken };

  const channelId = await findChannelId(zipToken);
  console.log(`  Found DM channel "${CHANNEL_NAME}" (id: ${channelId.slice(0, 8)}...)`);
  console.log('');
  console.log('READY. Press Enter to fire each line, in order.');
  console.log(`(${SCRIPT.length} lines scripted. Ctrl+C to quit.)`);
  console.log('');

  const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

  let cursor = 0;

  rl.on('line', async () => {
    if (cursor >= SCRIPT.length) {
      console.log('   (script exhausted - Ctrl+C to quit)');
      return;
    }

    const entry = SCRIPT[cursor];
    cursor++;

    try {
      await sendMessage(tokens[entry.sender], channelId, entry.text);
      const remaining = SCRIPT.length - cursor;
      console.log(`-> ${label(entry.sender)} (${cursor}/${SCRIPT.length}): "${preview(entry.text)}"`);
      if (remaining === 0) {
        console.log('   Last line sent. Ctrl+C to exit when done.');
      } else {
        const next = SCRIPT[cursor];
        console.log(`   Next: ${label(next.sender)}. Press Enter to fire.`);
      }
    } catch (err) {
      console.error('!! Failed to send:', err.message);
      cursor--; // let them retry the same line
    }
  });

  rl.on('close', () => {
    console.log('\nGoodbye.');
    process.exit(0);
  });
}

main().catch(err => {
  console.error('\n!! Fatal:', err.message);
  console.error('   Is the backend running on ' + BACKEND + '?');
  process.exit(1);
});
