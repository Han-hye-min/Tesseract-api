document.getElementById('processImage').addEventListener('click', () => {
    const fileInput = document.getElementById('imageUpload');
    const file = fileInput.files[0];

    if (!file) {
        alert("Please select an image file.");
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    fetch('/upload', {
        method: 'POST',
        body: formData,
    })
        .then((response) => response.json())
        .then((data) => {
            document.getElementById('output').value = data.text || 'No text found!';
        })
        .catch((error) => console.error('Error:', error));
});