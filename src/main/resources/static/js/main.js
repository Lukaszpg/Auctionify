$(document).ready(function () {
    enableSelectValidation();
    enableMaterializeSelects();
    searchClearIconClick();
    searchClearIconFadeout();
    searchBarOnFocusIn();
    searchBarOnFocusOut();
});

var enableSelectValidation = function() {
    $("select[required]").css({display: "block", height: 0, padding: 0, width: 0, position: 'absolute'});
}

var enableMaterializeSelects = function () {
    $('select').material_select();
}

var searchClearIconClick = function () {
    $(".search-clear-icon").click(function () {
        $("#search").val("");
    });
}

var searchBarOnFocusIn = function () {
    $("#search").focus(function () {
        $(".search-clear-icon").fadeIn(300);
    });
};

var searchBarOnFocusOut = function () {
    $("#search").focusout(function () {
        searchClearIconFadeout();
    });
};

var searchClearIconFadeout = function () {
    $(".search-clear-icon").fadeOut(300);
}

