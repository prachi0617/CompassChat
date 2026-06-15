import { useEffect, useState } from "react";
import {
    getResources,
    searchResources,
    sendSupportRequest
} from "../api.js";

export default function ResourcePage() {
    const [resources, setResources] = useState([]);
    const [keyword, setKeyword] = useState("");

    useEffect(() => {
        loadResources();
    }, []);

    async function loadResources() {
        const data = await getResources();
        setResources(data);
    }

    async function handleSearch(event) {
        event.preventDefault();

        if (!keyword.trim()) return;

        const data = await searchResources(keyword);
        setResources(data);
    }

    async function askForHelp(resource) {
        const result = await sendSupportRequest({
            sourceProject: "First Step",
            userId: 1,
            category: resource.typeOfService || "Resource Help",
            resourceTitle: resource.organizationName,
            supportType: "Resource Help",
            message: `The user needs help with this resource: ${resource.organizationName}`
        });

        alert(`Support request sent to ${result.routedChannel}`);
    }

    return (
        <div>
            <h1>Resources</h1>
            <p className="muted">First Step resource finder.</p>

            <form onSubmit={handleSearch} className="searchBar">
                <input
                    value={keyword}
                    onChange={(event) => setKeyword(event.target.value)}
                    placeholder="Search housing, food, advocacy..."
                />

                <button className="btn" type="submit">
                    Search
                </button>

                <button className="btn" type="button" onClick={loadResources}>
                    Reset
                </button>
            </form>

            {resources.map((resource, index) => (
                <div key={index} className="card">
                    <span className="badge">{resource.typeOfService}</span>

                    <h3>{resource.organizationName}</h3>
                    <p>{resource.servicesDescription}</p>
                    <p>{resource.fullAddress}</p>

                    {resource.phone && <p>Phone: {resource.phone}</p>}

                    {resource.website && (
                        <a href={resource.website} target="_blank" rel="noreferrer">
                            Website
                        </a>
                    )}

                    <div className="actions">
                        <button className="btn" onClick={() => askForHelp(resource)}>
                            Ask for Help
                        </button>
                    </div>
                </div>
            ))}
        </div>
    );
}