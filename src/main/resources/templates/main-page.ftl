<html>
<head>
    <!-- Bootstrap core CSS -->
    <link href="/mdbootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Material Design Bootstrap -->
    <link href="/mdbootstrap/css/mdb.min.css" rel="stylesheet">

    <!-- SCRIPTS -->
    <!-- JQuery -->
    <script type="text/javascript" src="/mdbootstrap/js/jquery-3.2.1.min.js"></script>
    <!-- Bootstrap tooltips -->
    <script type="text/javascript" src="/mdbootstrap/js/popper.min.js"></script>
    <!-- Bootstrap core JavaScript -->
    <script type="text/javascript" src="/mdbootstrap/js/bootstrap.min.js"></script>
    <!-- MDB core JavaScript -->
    <script type="text/javascript" src="/mdbootstrap/js/mdb.min.js"></script>
    <script src="/js/showUploadedImage.js"></script>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark primary-color fixed-top">
        <a class="navbar-brand" href="/">DressMeApp</a>
    </nav>
    <div class="container" style="padding-top: 90px">
        <#if model.isUserInWaiting>
            <h3 style="text-align: center">Извините, с Вашего последнего визита прошло не достаточно времени. Попробуйте позже.</h3>
        <#else>
        <div>
            <h3 style="text-align: center">Рекомендации по выбору одежды на основе Вашего телосложения</h3>
        </div>
        <div id="alert-message"></div>
        <form id="classifier-form" enctype="multipart/form-data" >
            <div class="form-group">
                <div class="row">
                    <div class="col-md-6">
                        <label for="face">Фотография в фас</label>
                        <input class="form-control" type="file" name="face" id="face" accept="image/*"><br>
                        <div class="row">
                            <div class="col-md-2"></div>
                            <div class="col-md-10">
                                <img id="face-image" src="" alt="Тут будет Ваш фас" width="300" height="400">
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <label for="profile">Фотография в профиль</label>
                        <input class="form-control" type="file" name="profile" id="profile" accept="image/*"><br>
                        <div class="row">
                            <div class="col-md-2"></div>
                            <div class="col-md-10">
                                <img id="profile-image" src="" alt="Тут будет Ваш профиль" width="300" height="400">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-md-4"></div>
                    <div id="submit-button-place" class="col-md-4">
                        <button onclick="uploadImagesForClassification()" class="btn btn-success form-control" type="button">получить рекомендации</button>
                    </div>
                    <div class="col-md-4"></div>
                </div>
            </div>
        </form>
        </#if>
        <div id="like-result"></div>
        <div id="result-row" class="row">
        </div>
    </div>
    <#if model.isUserInWaiting>
        <footer class="page-footer fixed-bottom font-small primary-color pt-4 mt-4">
    <#else>
        <footer class="page-footer font-small primary-color pt-4 mt-4">
    </#if>
        <div class="container-fluid text-center text-md-left">
            <div class="row">
                <div class="col-md-6">
                    <h6 class="text-uppercase font-weight-bold">
                        <strong>AIBegginers</strong>
                    </h6>
                    <hr class="deep-purple accent-2 mb-4 mt-0 d-inline-block mx-auto" style="width: 60px;">
                    <p>Предлагаем решения, которые подчеркнут Ваши достоинства и сгладят недостатки.</p>
                </div>
                <div class="col-md-6">
                    <h6 class="text-uppercase font-weight-bold">
                        <strong>Contact</strong>
                    </h6>
                    <hr class="deep-purple accent-2 mb-4 mt-0 d-inline-block mx-auto" style="width: 60px;">
                    <p>
                        <i class="fa glyphicon-home mr-3"></i> КФУ, Кремлевская 35, Казань, Россия</p>
                    <p>
                        <i class="fa glyphicon-envelope mr-3"></i> dressmeapp@example.com</p>
                </div>
            </div>
        </div>
        <div class="footer-copyright py-3 text-center">
            © 2018 Copyright:
            <a href="http://itis.kpfu.ru"> Высшая школа ИТИС КФУ </a>
        </div>
    </footer>


    <script>
        $( document ).ready(function start() {
            var faceInput = document.getElementById('face');
            faceInput.addEventListener('change', handleFileSelect, false);
            faceInput.outputId = 'face-image';

            var profileInput = document.getElementById('profile');
            profileInput.addEventListener('change', handleFileSelect, false);
            profileInput.outputId = 'profile-image';
        });

        function uploadImagesForClassification() {
            $("#alert-message").html("");
            var form = $('#classifier-form')[0];
            var data = new FormData(form);
            pasteResultLoadingImage();
            $.ajax({
                url: '/upload',
                type: 'POST',
                data: data,
                dataType: 'json',
                enctype: 'multipart/form-data',
                contentType: false,
                processData: false,
                success: function (data) {
                    pasteClassificationSubmitButton();
                    writeResult(data);
                },
                error: function () {
                    pasteErrorAlertMessage("Извините, что-то пошло не так. Попробуйте другие фотографии.");
                    pasteClassificationSubmitButton();
                    console.log('sendFileByAjax method error')
                }
            })
        }

        function pasteErrorAlertMessage(messageType) {
            $("#alert-message").html("");
            var message = messageType;
            if (messageType === "no-result") {
                message = "Извините, результат классификации данных фотографий имеет низкую точность.\nПожалуйста, попробуйте загрузить другие фотографии в соответствии с инструкцией."
            } else if (messageType === "failed") {
                message = "Извините, данные фотографии не могут быть подвержены классификации.\nПопробуйте загрузить другие фотографии в соответствии с инструкцией."
            } else if (messageType === "limit") {
                message = "Извините, превышен лимит запросов. Попробуйте немного позже - система будет умнее, мы Вам гарантируем.";
            }
            $("#alert-message").append(
                '<div class="alert alert-danger alert-dismissible">' +
                    '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
                    '<strong>Упсс...</strong> ' + message + ' ' +
                '</div>'
            );
        }

        function writeResult(data) {
            let resultRow = $('#result-row');
            var lookImage = data.lookImage;
            if (lookImage == null) {
                pasteErrorAlertMessage(data.type);
                return;
            }
            resultRow.html('');
            resultRow.append(
                '<div class="col-md-1"></div>' +
                '<div class="col-md-2" id="dislike-button">' +
                    '<img src="/images/dislike.jpg" width="100" height="100" alt="Не нравится" onclick="userSaidDislike(' + lookImage.id + ')">' +
                '</div>' +
                '<div class="col-md-2"></div>' +
                '<div class="col-md-3" id="image-look">' +
                    '<img src="/file/' + lookImage.fileInfo.id + '" width="auto" height="450">' +
                '</div>' +
                '<div class="col-md-2"></div>' +
                '<div class="col-md-2" id="like-button">' +
                    '<img src="/images/like.jpg" width="100" height="100" alt="Нравится" onclick="userSaidLike(' + lookImage.id + ')">' +
                '</div>'
            );
        }

        function clearResultRow() {
            $('#result-row').html("")
        }

        function userSaidLike(lookImageId) {
            clearLookButtons();
            pasteResultLoadingImage();
            $.ajax({
                url: '/look/' + lookImageId + "/like",
                type: 'GET',
                dataType: 'text',
                success: function (data) {
                    pasteClassificationSubmitButton();
                    writeLikeResult(data);
                },
                error: function () {
                    pasteErrorAlertMessage("Извините, что-то пошло не так. Попробуйте другие фотографии.");
                    pasteClassificationSubmitButton();
                    console.log('sendFileByAjax method error')
                }
            })
        }

        function clearLookButtons() {
            $('#like-button').html('');
            $('#dislike-button').html('');
        }

        function writeLikeResult(data) {
            let likeResultDiv = $('#like-result');
            $('#classifier-form').reset();
            console.log(data);
            likeResultDiv.append(
                    '<h3 style="text-align: center">Мы рады, что Вам понравился предложенный набор!</h3>'
            );

        }

        function userSaidDislike(lookImageId) {
            clearResultRow();
            pasteResultLoadingImage();
            $.ajax({
                url: '/look/' + lookImageId + "/dislike",
                type: 'GET',
                dataType: 'json',
                success: function (data) {
                    pasteClassificationSubmitButton();
                    writeResult(data);
                },
                error: function () {
                    pasteErrorAlertMessage("Извините, что-то пошло не так. Попробуйте другие фотографии.");
                    pasteClassificationSubmitButton();
                    console.log('sendFileByAjax method error')
                }
            })
        }


        function pasteClassificationSubmitButton() {
            $('#submit-button-place').html('');
            $('#submit-button-place').append(
                    '<button onclick="uploadImagesForClassification()" class="btn btn-success form-control" type="button">получить рекомендации</button>'
            )
        }

        function pasteResultLoadingImage() {
            $('#submit-button-place').html('');
            $('#submit-button-place').append(
                    '<img src="/images/loading.gif" width="300" height="300">'
            )
        }
    </script>
</body>
</html>