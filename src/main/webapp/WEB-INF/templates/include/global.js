$(document).bind("ajaxSend", function (b, c) {
    var a = _gtag.traceId + ":" + (++_gtag.ajaxCount);
    c.setRequestHeader("x-trace-id", a)
});
$(document).on("click", "ul.nav li.parent > a > span.icon", function () {
    $(this).find("em:first").toggleClass("glyphicon-minus")
});
$(".sidebar span.icon").find("em:first").addClass("glyphicon-plus");
var bootstrapDom;
var fixedHeaderObject;
if ($(window).width() < 768) {
    bootstrapDom = "<''<'col-sm-12'f>><''<'col-sm-12'tr>><''<'col-sm-6'l><'col-sm-6'i>><''<p>>";
    fixedHeaderObject = false
} else {
    bootstrapDom = "<''<'col-sm-6'l><'col-sm-6'f>><''<'col-sm-12'tr>><''<'col-sm-5'i><'col-sm-7'p>>";
    fixedHeaderObject = {headerOffset: $("#navtop").outerHeight()}
    $(function () {
        $("[data-toggle='tooltip']").tooltip()
    });
}
var lang = {
    sProcessing: "处理中...",
    sLengthMenu: "显示 _MENU_ 项结果",
    sZeroRecords: "没有匹配结果",
    sInfo: "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
    sInfoEmpty: "显示第 0 至 0 项结果，共 0 项",
    sInfoFiltered: "(由 _MAX_ 项结果过滤)",
    sInfoPostFix: "",
    sSearch: "搜索:",
    sUrl: "",
    sEmptyTable: "表中数据为空",
    sLoadingRecords: "载入中...",
    sInfoThousands: ",",
    oPaginate: {sFirst: "首页", sPrevious: "上页", sNext: "下页", sLast: "末页"},
    oAria: {sSortAscending: ": 以升序排列此列", sSortDescending: ": 以降序排列此列"},
    buttons: {colvis: "显示隐藏数据列"}
};
Date.prototype.Format = function (a) {
    var c = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "H+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        S: this.getMilliseconds()
    };
    if (/(y+)/.test(a)) {
        a = a.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length))
    }
    for (var b in c) {
        if (new RegExp("(" + b + ")").test(a)) {
            a = a.replace(RegExp.$1, (RegExp.$1.length == 1) ? (c[b]) : (("00" + c[b]).substr(("" + c[b]).length)))
        }
    }
    return a
};
$("#selectTime").click(function () {
    var b = $(this).text();
    var a = mapOptions[b];
    if ($("#timePicker").hasClass("hide")) {
        if (a != null && a.length != 0) {
            $("#startTime").attr("value", a.from);
            $("#endTime").attr("value", a.to);
            $("a").each(function () {
                if (this.text == b) {
                    $(this).addClass("time-active")
                }
            })
        }
        $("#timePicker").removeClass("hide")
    } else {
        $("#timePicker").addClass("hide");
        $("a").each(function () {
            $(this).removeClass("time-active")
        })
    }
});
var mapOptions = {
    "最近5分钟": {from: "5分钟前", to: "现在", start: "now() - 5m", end: "now()"},
    "最近15分钟": {from: "15分钟前", to: "现在", start: "now() - 15m", end: "now()"},
    "最近30分钟": {from: "30分钟前", to: "现在", start: "now() - 30m", end: "now()"},
    "最近1小时": {from: "1小时前", to: "现在", start: "now() - 1h", end: "now()"},
    "最近3小时": {from: "3小时前", to: "现在", start: "now() - 3h", end: "now()"},
    "最近6小时": {from: "6小时前", to: "现在", start: "now() - 6h", end: "now()"},
    "最近12小时": {from: "12小时前", to: "现在", start: "now() - 12h", end: "now()"},
    "最近24小时": {from: "24小时前", to: "现在", start: "now() - 24h", end: "now()"},
    "最近7天": {from: "7天前", to: "现在", start: "now() - 7d", end: "now()"},
    "最近30天": {from: "30天前", to: "现在", start: "now() - 30d", end: "now()"},
    "最近60天": {from: "60天前", to: "现在", start: "now() - 60d", end: "now()"},
    "最近90天": {from: "90天前", to: "现在", start: "now() - 90d", end: "now()"},
    "最近6个月": {from: "6个月前", to: "现在", start: getSixMonth(), end: "now()"},
    "最近1年": {from: "1年前", to: "现在", start: getOneYear(), end: "now()"},
    "最近2年": {from: "2年前", to: "现在", start: getTwoYear(), end: "now()"},
    "最近5年": {from: "5年前", to: "现在", start: getFiveYear(), end: "now()"},
    "昨天": {from: "昨天00:00", to: "昨天24:00", start: GetDateStr(-1), end: GetDateStr(0)},
    "前天": {from: "前天00:00", to: "前天24:00", start: GetDateStr(-2), end: GetDateStr(-1)},
    "七天前": {from: "7天前00:00", to: "7天前24:00", start: GetDateStr(-7), end: GetDateStr(-6)},
    "上周": {from: "上周日00:00", to: "上周日24:00", start: getLastWeekFirst(), end: getLastWeekLast()},
    "上个月": {from: "上个月第一天00:00", to: "上个月最后一天24:00", start: getLastMonthFirst(), end: getLastMonthLast()},
    "去年": {from: "去年第一天24:00", to: "去年最后一天24:00", start: getLastYearFirst(), end: getLastYearLast()},
    "今天": {
        from: "今天00:00",
        to: "今天24:00",
        start: GetDateStr(0, "00", "00", "00"),
        end: GetDateStr(1, "00", "00", "00")
    },
    "昨天至今": {from: "昨天00:00", to: "现在", start: GetDateStr(0, "00", "00", "00"), end: "now()"},
    "本周": {from: "本周一00:00", to: "本周日24:00", start: getWeekFirst(), end: getWeekLast()},
    "本周至今": {from: "本周一00:00", to: "现在", start: getWeekFirst(), end: "now()"},
    "本月": {from: "本月第一天00:00", to: "本月最后一天24:00", start: getMonthFirst(), end: getMonthLast()},
    "今年": {from: "今年第一天00:00", to: "今年最后一天24:00", start: getYearFirst(), end: getYearLast()},
};
function GetDateStr(e) {
    var b = new Date();
    b.setDate(b.getDate() + e);
    var f = b.getYear();
    var a = b.getMonth() + 1;
    var c = b.getDate();
    return new Date(b).Format("yyyy-MM-dd") + " 00:00:00"
}
function getWeekFirst() {
    var a = new Date();
    a.setDate(a.getDate() - a.getDay() + 1);
    return new Date(a).Format("yyyy-MM-dd") + " 00:00:00"
}
function getWeekLast() {
    var a = new Date();
    a.setDate(a.getDate() - a.getDay() + 8);
    return new Date(a).Format("yyyy-MM-dd") + " 00:00:00"
}
function getLastWeekFirst() {
    var a = new Date();
    a.setDate(a.getDate() - a.getDay() - 6);
    return new Date(a).Format("yyyy-MM-dd") + " 00:00:00"
}
function getLastWeekLast() {
    var a = new Date();
    a.setDate(a.getDate() - a.getDay());
    return new Date(a).Format("yyyy-MM-dd") + " 23:59:59"
}
function getMonthFirst() {
    var a = new Date();
    a.setMonth(a.getMonth());
    a.setDate(1);
    return new Date(a).Format("yyyy-MM-dd") + " 00:00:00"
}
function getMonthLast() {
    var a = new Date();
    a.setMonth(a.getMonth() + 1);
    a.setDate(0);
    return new Date(a).Format("yyyy-MM-dd") + " 23:59:59"
}
function getLastMonthFirst() {
    var a = new Date();
    a.setMonth(a.getMonth() - 1);
    a.setDate(1);
    return new Date(a).Format("yyyy-MM-dd") + " 00:00:00"
}
function getLastMonthLast() {
    var a = new Date();
    a.setMonth(a.getMonth());
    a.setDate(0);
    return new Date(a).Format("yyyy-MM-dd") + " 23:59:59"
}
function getSixMonth() {
    var a = new Date();
    a.setMonth(a.getMonth() - 6);
    return new Date(a).Format("yyyy-MM-dd HH:mm:ss")
}
function getYearFirst() {
    var a = new Date();
    a.setYear(a.getFullYear());
    a.setDate(1);
    return new Date(a).Format("yyyy-MM-dd") + " 00:00:00"
}
function getYearLast() {
    var a = new Date();
    a.setYear(a.getFullYear() + 1);
    a.setDate(0);
    return new Date(a).Format("yyyy-MM-dd") + " 23:59:59"
}
function getLastYearFirst() {
    var a = new Date();
    a.setYear(a.getFullYear() - 1);
    a.setDate(1);
    return new Date(a).Format("yyyy-MM-dd") + " 00:00:00"
}
function getLastYearLast() {
    var a = new Date();
    a.setYear(a.getFullYear());
    a.setDate(0);
    return new Date(a).Format("yyyy-MM-dd") + " 23:59:59"
}
function getOneYear() {
    var a = new Date();
    a.setYear(a.getFullYear() - 1);
    return new Date(a).Format("yyyy-MM-dd HH:mm:ss")
}
function getTwoYear() {
    var a = new Date();
    a.setYear(a.getFullYear() - 2);
    return new Date(a).Format("yyyy-MM-dd HH:mm:ss")
}
function getFiveYear() {
    var a = new Date();
    a.setYear(a.getFullYear() - 5);
    return new Date(a).Format("yyyy-MM-dd HH:mm:ss")
}
$("#quickRanges a").click(function () {
    var a = mapOptions[this.text];
    if (a != null && a.length != 0) {
        request(this.text, a.start, a.end)
    }
});
$("#timeRange").click(function () {
    var b = $("#startTime").val();
    var a = $("#endTime").val();
    if (verify(b, a)) {
        request("other", b, a)
    }
});
function verify(d, a) {
    var b = d.match(/^\d{4}-(?:0\d|1[0-2])-(?:[0-2]\d|3[01])( (?:[01]\d|2[0-3])\:[0-5]\d\:[0-5]\d)?$/);
    var c = a.match(/^\d{4}-(?:0\d|1[0-2])-(?:[0-2]\d|3[01])( (?:[01]\d|2[0-3])\:[0-5]\d\:[0-5]\d)?$/);
    if (b == null || c == null) {
        alert("请输入格式正确的日期\n\r日期格式：yyyy-MM-dd HH:mm:ss\n\r例如：2015-01-20 00:00:00\n\r");
        return false
    }
    return true
}
