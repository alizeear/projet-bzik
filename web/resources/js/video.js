document.addEventListener('DOMContentLoaded', function() {
    var v = document.getElementById('v');
    var canvas = document.getElementById('c');
    var context = canvas.getContext('2d');

    var cw = Math.floor(canvas.clientWidth / 100);
    var ch = Math.floor(canvas.clientHeight / 100);
    canvas.width = cw;
    canvas.height = ch;

    v.addEventListener('play', function() {
        draw(this, context, cw, ch);
    }, false);
}, false);

function draw(v, c, w, h) {
    if (v.paused || v.ended)
        return false;
    c.drawImage(v, 0, 0, w, h);
    setTimeout(draw, 100, v, c, w, h);
}

$(document).ready(function() {
    var videoPlayed = false,
            myVid = document.getElementById("v"),
            h = parseInt($("#heu").text()),
            m = parseInt($("#min").text()),
            s = parseInt($("#sec").text()),
            idRun = parseInt($('.courseId').text()),
            idRunner = parseInt($('.utilisateurId').text());

    /**
     * Ajustement de la taille de la boite de ranking 
     */
    rankH = parseInt($("#valRank").css("height")) + 5;
    rankW = parseInt($("#valRank").css("width")) + 20;
    $("#rank, #rank rect").attr({"width": rankW
        , "height": rankH});

    /**
     * Listener sur la fin de lecture de la video
     */
//    myVid.addEventListener('ended', function() {
//        $('#layout').fadeOut('slow');
//        console.log("video finished");
//        setCourseFinished(idRunner, idRun);
//        showRanking();
//    }, false);

    checkSpeed = setInterval(function(url) {
        $.ajax({
            type: "GET",
            url: "http://172.31.250.233:8080/bzik-all/rest/resultat/last/" + idRunner + "&" + idRun,
            success: function(data) {
                if ((data.vitesse && !videoPlayed) && $(".timeLeft").text().length < 1) {
                    $('#countDownLayout').css("background", "rgba(0,0,0,0)");
                    myVid.play();
                    ratio = data.vitesse / 50;
                    videoPlayed = true;
                } else if (data.vitesse) {
                    if ((data.vitesse < 0) || ($("#valDistance").data("distance") >= $("#v").data("distance"))) {
                        ratio = 0;
                        videoPlayed = false;
                        myVid.pause();
                        clearInterval(checkSpeed);
                        $('#layout').fadeOut('slow');
                        if (data.vitesse >= 0) {
                            setCourseFinished(idRunner, idRun);
                            showRank = setInterval(showRanking, 2000);
                        }
                    } else {
                        ratio = data.vitesse / 50;
                    }
                } else {
                    ratio = 0;
                }
                myVid.playbackRate = ratio;
                if (videoPlayed) {
                    refreshTime($("#valTemps"));
                    if (data.vitesse >= 0) {
                        changeSpeed(data.vitesse);
                        changeDistance(data.distance);
                    }
                }
            },
            error: function() {
                console.log("error - Server disconnected");
            }
        });
    }, 1000);

    checkRunners = setInterval(function() {
        if (videoPlayed) {
            $.ajax({
                type: "GET",
                // Recupere une liste des derniers resultat pour une course donnée
                url: "http://172.31.250.233:8080/bzik-all/rest/resultat/getRank/" + idRun + "&" + "distance" + "&" + "DESC",
                success: function(data) {
                    var string = "",
                            i = 1;
                    $("#valRank dd").remove();
                    $.each(data, function() {
                        string += "<dd>"
                                + i
                                + " - "
                                + this.utilisateur.pseudo
                                + "</dd>";
                        i++;
                    });
                    $("#valRank dt:first-child").after(string);
                    /**
                     * Ajustement de la taille de la boite de ranking 
                     */
                    rankH = parseInt($("#valRank").css("height")) + 5;
                    rankW = parseInt($("#valRank").css("width")) + 20;
                    $("#rank, #rank rect").attr({"width": rankW
                        , "height": rankH});

                },
                error: function() {
                    console.log("error - Server disconnected");
                }
            });
        }
    }, 1000);

    /**
     * On passe la distance en metre dans les parametres de la fonction. On calcule ensuite la moyenne et on affiche tout ça.
     * @param {type} dist
     * @returns {undefined}
     */
    function changeDistance(dist) {
        $("#length").text(dist);
        $("#valDistance").data("distance", dist);
        $("#progressBar progress").attr("value", dist);
    }

    /**
     * Fonction qui permet de rafraichir le temps dans la target
     * @param {type} target
     * @returns {undefined}
     */
    function refreshTime(target) {
        var tmpH, tmpM, tmpS;
        if (s == 59) {
            s = 0;
            if (m == 59) {
                m = 0;
                if (h == 23) {
                    h = 0;
                } else {
                    h++;
                }
            } else {
                m++;
            }
        } else {
            s++;
        }
        if (h < 10) {
            tmpH = "0" + h;
        } else {
            tmpH = h;
        }
        if (m < 10) {
            tmpM = "0" + m;
        } else {
            tmpM = m;
        }
        if (s < 10) {
            tmpS = "0" + s;
        } else {
            tmpS = s;
        }
        $("#heu").text(tmpH);
        $("#min").text(tmpM);
        $("#sec").text(tmpS);
        $(target).attr("data-temps", tmpH + ":" + tmpM + ":" + tmpS);
    }

    /**
     * 
     * @param {type} speed
     * @returns {undefined}
     */
    function changeSpeed(speed) {
        $("#kmh").text(speed);
        $("#valVitesse").attr("data-vitesse", speed);
    }


    /**
     * showRanking permet d'afficher le classement
     */
    function showRanking() {
        $.ajax({
            type: "GET",
            // Recupere une liste des derniers resultat pour une course donnée
            url: "http://172.31.250.233:8080/bzik-all/rest/resultat/getRank/" + idRun + "&" + "temps" + "&" + "ASC",
            success: function(data) {
                var string = "<dt>Classement :</dt>",
                        i = 1;
                var tmpH, tmpM, tmpS;
                $.each(data, function() {
                    var tmpTime = this.temps;
                    tmpS = tmpTime % 60;
                    tmpM = ((tmpTime - tmpS) / 60) % 60;
                    tmpH = (((tmpTime - tmpS) / 60 - tmpM) / 60);

                    if (tmpH < 10) {
                        tmpH = "0" + tmpH;
                    }
                    if (tmpM < 10) {
                        tmpM = "0" + tmpM;
                    }
                    if (tmpS < 10) {
                        tmpS = "0" + tmpS;
                    }
                    string += "<dd>"
                            + i
                            + " - "
                            + this.utilisateur.pseudo
                            + " - "
                            + "<span class=\"runnerTime\">"
                            + tmpH + "h" + tmpM + "m" + tmpS + "s";
                    +"</span>"
                            + "</dd>";
                    i++;
                });
                string += "<dt>Action :</dt>"
                        + "<dd><a href=\"./\" title=\"retour à l'accueil\">Accueil</a></dd>";
                $("#ranking dl").html(string);
            },
            error: function() {
                console.log("error - Server disconnected");
            }
        });
        $("#ranking").css("display", "flex");
        $("#").css("display", "block");
    }

    /**
     * setCourseFinished permet de définir la course courante 
     * comme finie
     * @param {type} utilisateurId
     * @param {type} courseId
     * @returns {undefined}
     */
    function setCourseFinished(utilisateurId, courseId) {
        $.ajax({
            type: "GET",
            url: "http://172.31.250.233:8080/bzik-all/rest/resultat/setCourseFinished/" + utilisateurId + "&" + courseId,
            success: function(data) {
                console.log('COURSE FINISHED !!!!');
            },
            error: function() {
                console.log("error - The course ending has not been set");
            }
        });
    }

}
);
