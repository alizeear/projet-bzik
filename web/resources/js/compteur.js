$(document).ready(function() {
    var intervalHeure,
            intervalMinute,
            intervalSeconde;

    start(12, 15, 36);

    function start(h, m, s) {
        $("#heure").html(h);
        $("#minute").html(m);
        $("#seconde").html(s);
    }

    function stop() {
        clearInterval(intervalHeure);
        clearInterval(intervalMinute);
        clearInterval(intervalSeconde);
    }
})
