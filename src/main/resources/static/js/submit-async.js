
window.addEventListener("load", (event) => {
  document.getElementById('fileInput').addEventListener('change',function(e) {
      e.preventDefault();
      document.getElementById('submit').click();

  });
  document.getElementById('uploadForm').addEventListener('submit', function(e) {
      e.preventDefault();
      const fileInput = document.getElementById('fileInput');
      const formData = new FormData();
      formData.append('file', fileInput.files[0]);

      fetch('/', {
          method: 'POST',
          body: formData
      })
      .then(response => response.json())
      .then(data => {
          document.getElementById('result').textContent = JSON.stringify(data, null, 2);
      })
      .catch(error => {
          document.getElementById('result').textContent = 'Error: ' + error;
      });
  });
});