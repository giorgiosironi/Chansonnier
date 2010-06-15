var Manager;

(function ($) {

  $(function () {
    Manager = new AjaxSolr.Manager({
      solrUrl: 'http://localhost:8983/solr/'
    });
    Manager.addWidget(new AjaxSolr.ResultWidget({
  		id: 'result',
  		target: '#result'
	}));
	var fields = [ 'Emotion' ];
	for (var i = 0, l = fields.length; i < l; i++) {
  		Manager.addWidget(new AjaxSolr.TagcloudWidget({
    		id: fields[i],
    		target: '#' + fields[i],
    		field: fields[i]
  		}));
	}
	Manager.addWidget(new AjaxSolr.CurrentSearchWidget({
  		id: 'currentsearch',
  		target: '#selection',
	}));
    Manager.init();
    Manager.store.addByValue('q', '*:*');
    var params = {
  		facet: true,
  		'facet.field': [ 'Emotion' ],
  		'facet.limit': 20,
  		'facet.mincount': 1,
  		'f.topics.facet.limit': 50,
  		'json.nl': 'map'
	};
	for (var name in params) {
  		Manager.store.addByValue(name, params[name]);
	}
    
    Manager.doRequest();
  });

})(jQuery);
