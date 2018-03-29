<html>
<head>
    <script src="/js/jquery.js"></script>
</head>
<body>
<form id="clothes-form" enctype="multipart/form-data">
    <div>
        <label for="sex">пол</label>
        <select id="sex" name="sex" onchange="updateBodyTypeSelect(this.value)">
            <option value="-1" selected>выберите пол</option>
        <#list model.sexValues as sex>
            <option id="${sex?index}" value=${sex}>${sex}</option>
        </#list>
        </select>
    </div>
    <div>
        <label for="bodyTypeSelect">тип телосложения</label>
        <select id="bodyTypeSelect" name="bodyType"></select>
    </div>
    <div>
        <label for="itemCategory">пол</label>
        <select id="itemCategory" name="itemCategory">
            <option value="-1" selected>выберите категорию предмета</option>
            <#list model.categories as category>
                <option id="${category?index}" value=${category}>${category}</option>
            </#list>
        </select>
    </div>
    <div>
        <input type="file" name="files" id="file" accept="image/*" multiple><br>
    </div>
    <button type="button" onclick="uploadClothesFiles()">загрузить данные</button>
</form>
<script>
    function uploadClothesFiles() {
        var form = $('#clothes-form')[0];
        var data = new FormData(form);
        $.ajax({
            url: '/clothes',
            type: 'POST',
            data: data,
            enctype: 'multipart/form-data',
            contentType: false,
            processData: false,
            success: function (data) {
                console.log(data)
            },
            error: function () {
                console.log('uploadClothesFiles method error')
            }
        })
    }

    function updateBodyTypeSelect(sexValue) {
        $('#bodyTypeSelect').html("");
        if (sexValue !== "1") {
            $.ajax({
                url: '/api/' + sexValue + '/body-types',
                type: 'GET',
                dataType: 'json',
                success: function (data) {
                    fillBodyTypeSelect(data);
                },
                error: function () {
                    console.log('updateBodyTypeSelect method failed')
                }
            })
        }
    }

    function fillBodyTypeSelect(data) {
        var bodyTypes = data;
        var bodyTypeSelect = document.getElementById("bodyTypeSelect");
        for (var i = 0; i < bodyTypes.length; i++) {
            var currentBodyType = bodyTypes[i];
            var option = document.createElement("option");
            option.value = currentBodyType;
            var bodyTypeNameText = document.createTextNode(currentBodyType);
            option.appendChild(bodyTypeNameText);
            bodyTypeSelect.appendChild(option);
        }
    }
</script>
</body>
</html>