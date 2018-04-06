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
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark primary-color fixed-top">
        <a class="navbar-brand" href="/">DressMeApp</a>
    </nav>
    <div class="container" style="padding-top: 90px">
        <#if model.error.isPresent()>
            <div class="alert alert-danger" role="alert">Логин или пароль введены неверно</div>
        </#if>
        <form role="Form" method="post" action="/login" accept-charset="UTF-8">
            <div class="form-group">
                <input type="text" name="login" placeholder="логин" class="form-control">
            </div>
            <div class="form-group">
                <input type="password" name="password" placeholder="пароль" class="form-control">
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-lg-2">
                        <input type="checkbox" name="remember-me">
                    </div>
                    <div class="col-lg-10">
                        <label> запомнить меня</label>
                    </div>
                    <div class="clearfix"></div>
                </div>
            </div>
            <div class="form-group">
                <button type="submit" class="btn btn-default">вход</button>
            </div>
        </form>
    </div>
    <footer class="page-footer fixed-bottom font-small primary-color pt-4 mt-4">
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
</body>
</html>