/**
 * One-button voice complaint recorder.
 * Uses the browser MediaRecorder API to record microphone audio, then
 * uploads the resulting audio blob to /complaints/voice, where the server
 * pipeline (transcribe -> translate -> classify -> notify admin) takes over.
 */
(function () {
    const recordBtn = document.getElementById("recordBtn");
    const statusEl = document.getElementById("recorderStatus");
    const form = document.getElementById("voiceComplaintForm");
    const audioInput = document.getElementById("audioBlobInput");
    const hiddenSubmitWrapper = document.getElementById("submitWrapper");

    if (!recordBtn) return; // Not on the complaints page.

    let mediaRecorder;
    let audioChunks = [];
    let isRecording = false;

    recordBtn.addEventListener("click", async () => {
        if (!isRecording) {
            await startRecording();
        } else {
            stopRecording();
        }
    });

    async function startRecording() {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
            statusEl.textContent = "Your browser does not support microphone recording. Please use the text option below.";
            return;
        }
        try {
            const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            audioChunks = [];
            mediaRecorder = new MediaRecorder(stream);

            mediaRecorder.ondataavailable = (e) => audioChunks.push(e.data);

            mediaRecorder.onstop = () => {
                const audioBlob = new Blob(audioChunks, { type: "audio/webm" });
                uploadRecording(audioBlob);
                stream.getTracks().forEach((track) => track.stop());
            };

            mediaRecorder.start();
            isRecording = true;
            recordBtn.classList.add("recording");
            recordBtn.textContent = "⏹";
            statusEl.textContent = "Recording... tap again to stop and submit.";
        } catch (err) {
            statusEl.textContent = "Microphone access was denied or unavailable. Please use the text option below.";
        }
    }

    function stopRecording() {
        if (mediaRecorder && isRecording) {
            mediaRecorder.stop();
            isRecording = false;
            recordBtn.classList.remove("recording");
            recordBtn.textContent = "🎙";
            statusEl.textContent = "Processing your complaint with AI (transcribing, translating, classifying)...";
        }
    }

    function uploadRecording(blob) {
        const formData = new FormData();
        formData.append("audio", blob, "complaint.webm");

        // Spring Security's CSRF token: fetch() bypasses Thymeleaf's
        // automatic <form> token injection, so it's attached manually here.
        const csrfToken = document.querySelector('meta[name="_csrf"]');
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
        const headers = {};
        if (csrfToken && csrfHeaderMeta) {
            headers[csrfHeaderMeta.content] = csrfToken.content;
        }

        fetch(form.action, {
            method: "POST",
            headers: headers,
            body: formData,
        })
            .then((res) => res.text())
            .then((html) => {
                document.open();
                document.write(html);
                document.close();
            })
            .catch(() => {
                statusEl.textContent = "Upload failed. Please check your connection and try again.";
            });
    }
})();
