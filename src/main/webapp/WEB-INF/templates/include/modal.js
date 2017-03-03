var value;
var name;
var principal;
var introduction;
$("#datatable tbody").on('click', 'td', function () {
    value = table.cell(this).data();
    var rowIdx = table.cell(this).index().row;
    name = table.cell(rowIdx, 0).data();
    var idx = table.cell(this).index().column;
    var title = table.column(idx).header();
    if ($(title).html() == "说明") {
        $('#warningInfo').addClass("hide");
        $('#dangerInfo').addClass("hide");
        $('#titleName').text(" [" + name + "]");
        $('#myModal').modal('show');
        if (value.indexOf("-[") >= 0) {
            var first = value.lastIndexOf("-[");
            var second = value.indexOf("]");
            introduction = value.substring(0, first);
            principal = value.substring(first + 2, second);
        } else {
            introduction = value.substring(0);
            principal = "";
        }
        $('#principal').val(principal);
        $('#introduction').val(introduction);
    }
    ;
});
$("#confirm").on('click', function () {
    var desc = $('#introduction').val();
    var prin = $('#principal').val();

    if (desc == introduction && prin == principal) {
        $('#warningInfo').removeClass("hide")
        $('#warningInfo').text("提示：您未做任何改动");
    } else {
        if (desc.indexOf("-[") >= 0 || prin.indexOf("-[") >= 0) {
            $('#warningInfo').removeClass("hide")
            $('#warningInfo').text("提示：您的修改信息中有非法字符串 \"-[\" ");
        } else {
            var markList = [];
            var dataObject = {
                service: name,
                info: desc,
                owner: prin
            };
            markList.push(dataObject);
            $.ajax({
                url: "${CONTEXT_PATH}/edit/mark",
                contentType: "application/json",
                data: JSON.stringify(markList),
                dataType: "json",
                type: "POST",
                traditional: true,
                success: function (data) {
                    if (data.code == 200) {
                        location.reload();
                    } else {
                        $('#dangerInfo').text("警告：" + data.info);
                        $('#dangerInfo').removeClass("hide");
                    }
                }
            });
        }
    }
})
