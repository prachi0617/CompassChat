import { useState } from "react";
import { uploadAttachment } from "../api/attachmentApi.js";

export default function AttachmentUpload({ messageId }) {
    const [file, setFile] = useState(null);
    const [status, setStatus] = useState("");

    async function handleUpload() {
        if (!file) {
            setStatus("Please choose a file first.");
            return;
        }

        try {
            await uploadAttachment(messageId, file);
            setStatus("File attached successfully.");
            setFile(null);
        } catch (error) {
            setStatus("File upload failed. Check backend.");
        }
    }

    return (
        <div className="attachmentBox">
            <input
                type="file"
                onChange={(event) => setFile(event.target.files[0])}
            />

            <button type="button" className="btn" onClick={handleUpload}>
                Attach File
            </button>

            {status && <p className="muted">{status}</p>}
        </div>
    );
}