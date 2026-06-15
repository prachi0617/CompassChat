import { useEffect, useState } from "react";
import { getMyMoods, logMood } from "../api/moodApi.js";

const MOOD_OPTIONS = [
    "HOPEFUL",
    "HAPPY",
    "STRESSED",
    "LONELY",
    "SAD",
    "ANXIOUS",
    "CALM",
    "ENERGETIC"
];

export default function MoodPage() {
    const [moods, setMoods] = useState([]);
    const [moodType, setMoodType] = useState("HOPEFUL");
    const [note, setNote] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        loadMoods();
    }, []);

    async function loadMoods() {
        try {
            const page = await getMyMoods();
            setMoods(page.content || page);
        } catch {
            setMoods([]);
        }
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");

        try {
            const result = await logMood(moodType, note);
            setMoods((current) => [result, ...current]);
            setNote("");
        } catch (err) {
            setError(err.response?.data?.message || "Failed to log mood");
        }
    }

    return (
        <div>
            <h1>Mood Check-In</h1>
            <p className="muted">
                Log how you're feeling and track your well-being over time.
            </p>

            <form onSubmit={handleSubmit} className="card">
                <h3>How are you feeling?</h3>

                <select
                    value={moodType}
                    onChange={(e) => setMoodType(e.target.value)}
                >
                    {MOOD_OPTIONS.map((m) => (
                        <option key={m} value={m}>
                            {m.charAt(0) + m.slice(1).toLowerCase()}
                        </option>
                    ))}
                </select>

                <input
                    value={note}
                    onChange={(e) => setNote(e.target.value)}
                    placeholder="Add a note (optional)..."
                />

                {error && <p className="error">{error}</p>}

                <button className="btn" type="submit">
                    Log Mood
                </button>
            </form>

            <h2>History</h2>

            {moods.length === 0 && (
                <p className="muted">No mood entries yet.</p>
            )}

            {moods.map((entry) => (
                <div key={entry.id} className="card">
                    <span className="badge">{entry.moodType}</span>
                    <p>{entry.note || "No note"}</p>
                    <small className="muted">{entry.timeAgo || entry.loggedAt}</small>
                </div>
            ))}
        </div>
    );
}
