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
    Manager.init();
    Manager.store.addByValue('q', 'Title:*');
    Manager.doRequest();
  });

})(jQuery);
