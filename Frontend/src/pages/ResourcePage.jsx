import { useEffect, useState } from "react";
import { getResources, searchResources } from "../api/resourceApi.js";

export default function ResourcePage() {
    const [resources, setResources] = useState([]);
    const [keyword, setKeyword] = useState("");

    useEffect(() => {
        loadResources();
    }, []);

    async function loadResources() {
        try {
            const data = await getResources();
            setResources(data);
        } catch {
            setResources([]);
        }
    }

    async function handleSearch(event) {
        event.preventDefault();
        if (!keyword.trim()) {
            loadResources();
            return;
        }
        try {
            const data = await searchResources(keyword);
            setResources(data);
        } catch {
            setResources([]);
        }
    }

    return (
        <div>
            <h1>Resources</h1>
            <p className="muted">
                Find housing, food, advocacy, and community services.
            </p>

            <form onSubmit={handleSearch} className="searchBar">
                <input
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                    placeholder="Search housing, food, advocacy..."
                />
                <button className="btn" type="submit">
                    Search
                </button>
                <button className="btn" type="button" onClick={loadResources}>
                    Reset
                </button>
            </form>

            {resources.length === 0 && (
                <p className="muted">No resources found.</p>
            )}

            {resources.map((resource, index) => (
                <div key={resource.id || index} className="card">
                    {resource.typeOfService && (
                        <span className="badge">{resource.typeOfService}</span>
                    )}
                    <h3>{resource.organizationName || "Unnamed Resource"}</h3>
                    <p>{resource.servicesDescription || "No description available."}</p>
                    {resource.fullAddress && <p>{resource.fullAddress}</p>}
                    {resource.phone && <p>Phone: {resource.phone}</p>}
                    {resource.website && (
                        <a
                            href={resource.website}
                            target="_blank"
                            rel="noreferrer"
                        >
                            Visit Website
                        </a>
                    )}
                </div>
            ))}
        </div>
    );
}
