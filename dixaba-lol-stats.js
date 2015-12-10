(function() {
$(".dixaba-statistics").remove();
var s = document.createElement('div');
s.className = "dixaba-statistics";
s.innerHTML = "<div style='z-index: 1000; display: block; font-size: 10px; color: #660000; background-color: #ffffff; margin: 35px; padding: 5px; position: fixed; border: #000000; border-radius: 5px; border-style: solid'><b>"+$(".game-summary").length+" игр<br>"+$(".game-summary-victory").length+" побед<br>"+$(".game-summary-defeat").length+" поражений<br>Винрейт: "+($(".game-summary-victory").length/$(".game-summary").length*100).toPrecision(4)+"%</b></div>";
document.body.insertBefore(s, document.body.firstChild);
}).call(this);
