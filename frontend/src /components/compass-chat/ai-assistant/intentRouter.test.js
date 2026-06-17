import { test } from 'node:test'
import assert from 'node:assert/strict'
import { classifyIntent } from './intentRouter.js'

test('detects urgent intent from "talk to a human"', () => {
    const result = classifyIntent('I need to talk to a human')
    assert.equal(result.intent, 'URGENT')
})

test('detects urgent intent even when mood keywords are also present', () => {
    const result = classifyIntent('I am sad and need a real person right now')
    assert.equal(result.intent, 'URGENT')
})

test('detects distressed mood from "overwhelmed"', () => {
    const result = classifyIntent("I'm feeling overwhelmed and need help finding housing")
    assert.equal(result.intent, 'MOOD')
    assert.equal(result.distressed, true)
})

test('detects non-distressed mood from "tired"', () => {
    const result = classifyIntent('I have just been so tired lately')
    assert.equal(result.intent, 'MOOD')
    assert.equal(result.moodType, 'LOW')
    assert.equal(result.distressed, false)
})

test('detects housing resource intent', () => {
    const result = classifyIntent('I need help finding housing')
    assert.equal(result.intent, 'RESOURCE')
    assert.equal(result.subProject, 'homematch')
})

test('detects youth resource intent', () => {
    const result = classifyIntent('I am aging out of foster care soon')
    assert.equal(result.intent, 'RESOURCE')
    assert.equal(result.subProject, 'futurepath')
})

test('falls back to general intent for unrelated text', () => {
    const result = classifyIntent('what time is the meeting tomorrow')
    assert.equal(result.intent, 'GENERAL')
})

test('handles empty input gracefully', () => {
    const result = classifyIntent('')
    assert.equal(result.intent, 'GENERAL')
})