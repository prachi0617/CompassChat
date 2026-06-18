import subProjectData from '../../../lib/resources.json'

const URGENT_KEYWORDS = [
    'help me',
    'human',
    'emergency',
    'real person',
    'talk to someone',
    'talk to a human',
    'need someone',
    'crisis',
]

// Keywords mapped to MoodType-style buckets. Distressed moods trigger the
// crisis safety-net block before anything else.
const MOOD_KEYWORDS = {
    VERY_LOW: ['suicidal', 'hopeless', 'want to die', "can't go on", 'worthless'],
    LOW: ['sad', 'depressed', 'down', 'lonely', 'tired', 'exhausted', 'overwhelmed', 'anxious', 'stressed', 'scared', 'worried'],
    NEUTRAL: ['okay', 'meh', 'unsure', 'confused'],
    GOOD: ['happy', 'calm', 'good', 'fine', 'relieved'],
    GREAT: ['great', 'excited', 'grateful', 'hopeful'],
}

const DISTRESSED_MOODS = new Set(['VERY_LOW'])
// "overwhelmed" specifically should still surface crisis resources per the
// plan's demo script, even though it's bucketed as LOW rather than VERY_LOW.
const DISTRESSED_KEYWORDS = new Set(['overwhelmed', 'hopeless', "can't go on", 'suicidal', 'want to die']);

function normalize(text) {
    return text.toLowerCase().trim()
}

function matchesAny(haystack, keywords) {
    return keywords.some((kw) => haystack.includes(kw))
}

const NEGATION_WORDS = ['not ', "don't ", "doesn't ", "didn't ", "isn't ", "aren't ", "can't ", "cannot ", 'no ', 'never ', "i'm not "]

function isNegated(text, keyword) {
    const idx = text.indexOf(keyword)
    if (idx === -1) return false
    const preceding = text.slice(Math.max(0, idx - 30), idx)
    return NEGATION_WORDS.some((neg) => preceding.includes(neg))
}

/**
 * Classifies a free-text message into one of: URGENT, MOOD, RESOURCE, GENERAL.
 * Pure function — no side effects, no network calls. Order of checks matters:
 * urgent phrasing always wins, even if mood/resource keywords are also present.
 */
export function classifyIntent(rawText) {
    const text = normalize(rawText || '')

    if (!text) {
        return { intent: 'GENERAL' }
    }

    if (matchesAny(text, URGENT_KEYWORDS)) {
        return { intent: 'URGENT' }
    }

    for (const [moodType, keywords] of Object.entries(MOOD_KEYWORDS)) {
        for (const kw of keywords) {
            if (!text.includes(kw)) continue
            if ((moodType === 'GOOD' || moodType === 'GREAT') && isNegated(text, kw)) continue
            const distressed = DISTRESSED_MOODS.has(moodType) || DISTRESSED_KEYWORDS.has(kw)
            return { intent: 'MOOD', moodType, note: rawText, distressed }
        }
    }

    for (const project of subProjectData.subProjects) {
        if (matchesAny(text, project.keywords)) {
            return { intent: 'RESOURCE', subProject: project.slug }
        }
    }

    return { intent: 'GENERAL' }
}