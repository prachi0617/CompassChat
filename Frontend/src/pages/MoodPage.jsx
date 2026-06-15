import { useEffect, useState } from "react";
import { createMood, getMoods, sendSupportRequest } from "../api.js";

export default function MoodPage() {
    const [moods, setMoods] = useState([]);
    const [mood, setMood] = useState("Hopeful");
    const [note, setNote] = useState("");

    useEffect(() => {
        async function load() {
            const data = await getMoods();
            setMoods(data);
        }

        load();
    }, []);

    async function saveMood(event) {
        event.preventDefault();

        const saved = await createMood({
            mood,
            note,
            date: new Date().toISOString().slice(0, 10)
        });

        setMoods((current) => [saved, ...current]);
        setNote("");
    }

    async function requestSupport() {
        const result = await sendSupportRequest({
            sourceProject: "KindConnect",
            userId: 1,
            category: "Well-Being",
            resourceTitle: "Mood Check-In",
            supportType: "Mood Check-In",
            message: `The user reported feeling ${mood}. Note: ${note || "No note"}`
        });

        alert(`Support request sent to ${result.routedChannel}`);
    }

    return (
        <div>
            <h1>Mood Check-In</h1>
            <p className="muted">Track mood and request support.</p>

            <form onSubmit={saveMood} className="card">
                <h3>Today's Mood</h3>

                <select value={mood} onChange={(event) => setMood(event.target.value)}>
                    <option>Hopeful</option>
                    <option>Happy</option>
                    <option>Stressed</option>
                    <option>Lonely</option>
                    <option>Sad</option>
                </select>

                <input
                    value={note}
                    onChange={(event) => setNote(event.target.value)}
                    placeholder="Add note..."
                />

                <div className="actions">
                    <button className="btn" type="submit">
                        Save Mood
                    </button>

                    <button className="btn" type="button" onClick={requestSupport}>
                        Request Support
                    </button>
                </div>
            </form>

            <h2>Mood History</h2>

            {moods.map((item) => (
                <div key={item.id} className="card">
                    <h3>{item.mood}</h3>
                    <p>{item.date}</p>
                    <p>{item.note}</p>
                </div>
            ))}
        </div>
    );
}