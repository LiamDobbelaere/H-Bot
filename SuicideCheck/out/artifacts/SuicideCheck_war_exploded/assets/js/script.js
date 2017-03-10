(function() {
    $(function() {
        $("form").on("submit", formSubmit);
    });

    function formSubmit(e) {
        e.preventDefault();
        $("#result").text("Querying...");

        $.ajax({
            method: "GET",
            url: "api/v1/classify",
            data: {
                q: $("#input").val()
            }
        }).done(function(data) {
            $("#result").text("Result: " + data.result);
        })
    }
})();