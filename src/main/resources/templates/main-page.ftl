<html>
    <head>
        <link href="/css/styles.css" rel="stylesheet"/>
        <script src="/js/jquery.js"></script>
        <script>
            function sendFileByAjax(file) {
                var formData = new FormData();
                formData.append("file", file);
                writeResultLoadingImage();
                $.ajax({
                    url: '/upload',
                    type: 'POST',
                    data: formData,
                    dataType: 'json',
                    contentType: false,
                    processData: false,
                    success: function (data) {
                        writeResult(data);
                    },
                    error: function () {
                        console.log('sendFileByAjax method error')
                    }
                })
            }

            function writeResult(data) {
                var resultText = $('#inside-result-text');
                resultText.html('');
                resultText.append('<h2>' + data.type + '</h2>');
                var advices = data.advices;
                for (var i = 0; i < advices.length; i++) {
                    console.log(advices[i]);
                    $('#inside-result-text').append(
                            '<li>' + advices[i] + '</li>'
                    )
                }
            }

            function writeResultLoadingImage() {
                $('#inside-result-text').html('');
                $('#inside-result-text').append(
                        '<img src="/images/loading.gif" width="400" class="loading-image">'
                )
            }
        </script>
    </head>
    <body>
    <div class="header">
        <div id="inside-header-text">
            DressMeApp
        </div>
    </div>
    <div class="data">
        <div class="left-half">
            <div id = "file-form">
                <div id="file-form-title">
                    Classify your body type
                </div>
                <div id="file-form-form">
                    <form enctype="multipart/form-data">
                        <div class="file-form-button">
                            <input type="file" name="file" id="file" accept="image/*"><br>
                        </div>
                        <div class="file-form-button">
                            <button onclick="sendFileByAjax(($('#file'))[0]['files'][0])" class="btn" type="button">Upload file</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="right-half">
            <div id="result">
                <ul id="inside-result-text">
                </ul>
            </div>
        </div>
    </div>
    <div class="footer">
        <div id="inside-footer-text">
            AI Beginners
        </div>
    </div>
    </body>
</html>