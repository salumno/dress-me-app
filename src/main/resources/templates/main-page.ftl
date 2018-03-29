<html>
    <head>
        <link href="/css/styles.css" rel="stylesheet"/>
        <script src="/js/jquery.js"></script>
        <script>
            function uploadImagesForClassification() {
                var form = $('#classifier-form')[0];
                var data = new FormData(form);
                writeResultLoadingImage();
                $.ajax({
                    url: '/upload',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    enctype: 'multipart/form-data',
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
                let resultText = $('#inside-result-text');
                resultText.html('');
                var clothes = data;
                for (var i = 0; i < clothes.length; i++) {
                    var fileInfoId = clothes[i].fileInfo.id;
                    resultText.append(
                        '<img src="/file/' + fileInfoId + '" width="400">'
                    );
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
                    <form id="classifier-form" enctype="multipart/form-data" >
                        <div class="file-form-button">
                            <label for="select-sex"></label>
                            <select id="select-sex" name="sex" form="classifier-form">
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </select>
                        </div>
                        <div class="file-form-button">
                            <label for="face">Фотография в фас</label>
                            <input type="file" name="face" id="face" accept="image/*"><br>
                        </div>
                        <div class="file-form-button">
                            <label for="profile">Фотография в профиль</label>
                            <input type="file" name="profile" id="profile" accept="image/*"><br>
                        </div>
                        <div class="file-form-button">
                            <button onclick="uploadImagesForClassification()" class="btn" type="button">Upload file</button>
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