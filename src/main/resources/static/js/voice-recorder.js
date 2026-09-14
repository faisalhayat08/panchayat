/**
 * One-button live voice complaint recorder.
 * Combines:
 * 1. Browser SpeechRecognition (Web Speech API) for real-time, live voice-to-text
 *    in Hindi, English, and regional languages.
 * 2. MediaRecorder API to capture the real microphone audio stream.
 * 3. Submits both the real spoken transcript and audio recording to the AI backend.
 */
(function () {
    const recordBtn = document.getElementById("recordBtn");
    const statusEl = document.getElementById("recorderStatus");
    const form = document.getElementById("voiceComplaintForm");
    const spokenLangSelect = document.getElementById("spokenLangSelect");
    const livePreviewBox = document.getElementById("liveSpeechPreview");
    const liveTranscriptText = document.getElementById("liveTranscriptText");

    if (!recordBtn) return; // Not on complaints page

    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;

    let mediaRecorder = null;
    let audioChunks = [];
    let isRecording = false;
    let recognition = null;
    let finalTranscript = "";
    let interimTranscript = "";

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
            finalTranscript = "";
            interimTranscript = "";

            mediaRecorder = new MediaRecorder(stream);
            mediaRecorder.ondataavailable = (e) => {
                if (e.data && e.data.size > 0) audioChunks.push(e.data);
            };

            mediaRecorder.onstop = () => {
                const audioBlob = new Blob(audioChunks, { type: "audio/webm" });
                uploadRecording(audioBlob);
                stream.getTracks().forEach((track) => track.stop());
            };

            // Start audio recording
            mediaRecorder.start(250);
            isRecording = true;
            recordBtn.classList.add("recording");
            recordBtn.textContent = "⏹";
            statusEl.textContent = "Recording your voice... speak your problem clearly, then tap to stop.";

            // Start live speech-to-text recognition
            if (SpeechRecognition) {
                try {
                    recognition = new SpeechRecognition();
                    recognition.continuous = true;
                    recognition.interimResults = true;
                    recognition.lang = spokenLangSelect ? spokenLangSelect.value : "hi-IN";

                    recognition.onresult = (event) => {
                        let currentInterim = "";
                        for (let i = event.resultIndex; i < event.results.length; ++i) {
                            if (event.results[i].isFinal) {
                                finalTranscript += event.results[i][0].transcript + " ";
                            } else {
                                currentInterim += event.results[i][0].transcript;
                            }
                        }
                        interimTranscript = currentInterim;
                        const fullText = (finalTranscript + interimTranscript).trim();
                        if (fullText && liveTranscriptText) {
                            liveTranscriptText.textContent = fullText;
                        }
                    };

                    recognition.onerror = (e) => {
                        console.warn("Speech recognition notice:", e.error);
                    };

                    recognition.onend = () => {
                        if (isRecording && recognition) {
                            try {
                                recognition.start();
                            } catch (_) {}
                        }
                    };

                    recognition.start();

                    if (livePreviewBox) {
                        livePreviewBox.style.display = "block";
                        liveTranscriptText.textContent = "Listening to your voice... start speaking now.";
                    }
                } catch (recErr) {
                    console.warn("Speech recognition error:", recErr);
                }
            } else {
                if (livePreviewBox) {
                    livePreviewBox.style.display = "block";
                    liveTranscriptText.textContent = "Recording audio (Live speech preview not supported in this browser; backend will transcribe).";
                }
            }
        } catch (err) {
            console.error("Microphone access error:", err);
            statusEl.textContent = "Microphone access was denied or unavailable. Please check browser permissions or use text below.";
        }
    }

    function stopRecording() {
        if (!isRecording) return;
        isRecording = false;

        recordBtn.classList.remove("recording");
        recordBtn.textContent = "🎙";
        statusEl.textContent = "Processing your complaint with AI (transcribing, translating, classifying)...";

        if (recognition) {
            try {
                recognition.onend = null;
                recognition.stop();
            } catch (_) {}
        }

        if (mediaRecorder && mediaRecorder.state !== "inactive") {
            mediaRecorder.stop();
        }
    }

    function uploadRecording(blob) {
        const fullSpokenText = (finalTranscript + " " + interimTranscript).trim();
        const selectedLangOption = spokenLangSelect ? spokenLangSelect.options[spokenLangSelect.selectedIndex] : null;
        const langLabel = selectedLangOption ? selectedLangOption.text : "Hindi / Hinglish";

        const formData = new FormData();
        formData.append("audio", blob, "complaint.webm");
        if (fullSpokenText) {
            formData.append("transcript", fullSpokenText);
        }
        formData.append("language", langLabel);

        // Spring Security CSRF token header
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
            .catch((err) => {
                console.error("Upload error:", err);
                statusEl.textContent = "Upload failed. Please check your connection and try again.";
            });
    }
})();

