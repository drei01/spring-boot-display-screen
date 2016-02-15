(function(){
  "use strict"
  var root = this,
      $ = root.jQuery;
  if(typeof root.matrix === 'undefined'){ root.matrix = {} }

  var visitors = {
    terms: [],
    newTerms: [],
    $el: false,
    nextRefresh: 0,

    endpoint: function(profileId){
      return "/google/realtime?"
        + "ids=ga:"+ profileId +"&"
        + "metrics=ga:activeVisitors";
    },
    safeTerm: function(term){
      // Nothing that looks like an email address
      if(term.indexOf('@') > -1){
        return false;
      }
      // Nothing that is just a numeber
      if(term.match(/^[0-9\s]+$/)){
        return false;
      }
      // Nothing that is like a NI number
      if(term.match(/^[A-Za-z]{2}\s+?[0-9]{2}\s+?[0-9]{2}\s+?[0-9]{2}\s+?[A-Za-z]$/)){
        return false;
      }
      // No 503 requests
      if(term === "Sorry, we are experiencing technical difficulties (503 error)"){
        return false;
      }
      return true;
    },
    addTerm: function(term, count){
      var i, _i;
      for(i=0, _i=visitors.terms.length; i<_i;  i++){
        if(visitors.terms[i].term === term){
          visitors.terms[i].nextTick = count;
          return true;
        }
      }
      visitors.terms.push({
        term: term,
        total: 0,
        nextTick: count,
        currentTick: 0,
      });
    },
    zeroNextTicks: function(){
      var i, _i, newTerms = [];
      for(i=0, _i=visitors.terms.length; i<_i;  i++){
        visitors.terms[i].nextTick = 0;
      }
    },
    addNextTickValues: function(data){
      var i, _i, term;
      if(!data.totalsForAllResults) return;
      visitors.$el.html('<h1>' + root.matrix.numberWithCommas(data.totalsForAllResults['ga:activeVisitors']) + '</h1>');
    },
    addTimeIndexValues: function(){
      var i, _i, j, _j, term, time, newPeople, nonZeroTerms = [];
      for(i=0, _i=visitors.terms.length; i<_i;  i++){
        term = visitors.terms[i];
        newPeople = term.currentTick < term.nextTick ? term.nextTick - term.currentTick : 0;
        term.total = term.total + newPeople;
        term.currentTick = term.nextTick;
        if(newPeople > 0){
          for(j=0,_j=newPeople; j<_j; j++){
            visitors.newTerms.push(term.term);
          }
        }
        if(term.currentTick > 0){
          nonZeroTerms.push(term);
        }
      }
      visitors.newTerms.sort(function(){ return Math.floor((Math.random() * 3) - 1) });
      visitors.terms = nonZeroTerms;
    },
    parseResponse: function(data){
      var term, i, _i;

      visitors.zeroNextTicks();
      visitors.addNextTickValues(data);
      visitors.addTimeIndexValues();
    },
    displayResults: function(){
      var term = visitors.newTerms.pop();
      if(term){
        visitors.$el.prepend('<li>'+$('<div>').text(term).html()+'</li>');
        visitors.$el.css('margin-top',-visitors.$el.find('li').first().outerHeight(true)).animate({'margin-top':0},
        function(){
          visitors.$el.find('li:gt(20)').remove();
          root.setTimeout(visitors.displayResults, (visitors.nextRefresh - Date.now())/visitors.newTerms.length);
        })
      } else {
        root.setTimeout(visitors.displayResults, 5e3);
      }
    },
    init: function(){
      visitors.$el = $('#traffic-count');

      visitors.reload();
      visitors.displayResults();
      window.setInterval(visitors.reload, 60e3);
    },
    reload: function(){
      var endpoint = visitors.endpoint(root.matrix.settings.profileId);

      visitors.nextRefresh = Date.now() + 60e3;
      $.ajax({ dataType: 'json', url: endpoint, success: visitors.parseResponse});
    }
  };

  root.matrix.visitors = visitors;
}).call(this);
