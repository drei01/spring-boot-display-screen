(function(){
  "use strict"
  var root = this,
      $ = root.jQuery;
  if(typeof root.matrix === 'undefined'){ root.matrix = {} }

  var traffic = {
    points: 720,

    endpoint: function(profileId){
	    return "/data?"
	      + "ids=ga:"+ profileId +"&"
	      + "metrics=ga:sessions&"
	      + "start-date=yesterday&"
	      + "end-date=today&"
	      + "dimensions=ga:hour&"
	      + "max-results=50";
	  },
    parseResponse: function(data){
      var counts = [],
          i, _i;
      for(i=0,_i=data.rows.length; i<_i; i++){
        counts.unshift(parseInt(data.rows[i][1], 10));
      }
      if(typeof traffic.sparkline === 'undefined'){
        traffic.sparkline = root.matrix.sparklineGraph('#traffic-count-graph', { data: counts, points: data.rows, height: 120, width: traffic.$graphEl.width() });
        traffic.sparkline.update(counts, "Sessions over the past " + (Math.round(traffic.points / 30)) + " hours");
      } else {
        traffic.sparkline.update(counts, "Sessions over the past " + (Math.round(traffic.points / 30)) + " hours");
      }
    },
    init: function(){
      traffic.$graphEl = $('#traffic-count-graph');

      traffic.reload();
      window.setInterval(traffic.reload, 20e3);
    },
    reload: function(){
      var endpoint = traffic.endpoint(root.matrix.settings.profileId);

      $.ajax({ dataType: 'json', url: endpoint, success: traffic.parseResponse});
    }
  };

  root.matrix.traffic = traffic;
}).call(this);
