function handleFileSelect(evt) {
    var outputId = evt.target.outputId;
    var files = evt.target.files;
    var file = files[0];
    if (file.type.match('image.*')) {
        var reader = new FileReader();
        reader.onload = (function() {
            return function(e) {
                document.getElementById(outputId).src = e.target.result;
            };
        })(file);
        reader.readAsDataURL(file);
    }
}