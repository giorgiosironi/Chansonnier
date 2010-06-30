var Manager;

(function ($) {

  $(function () {
    Manager = new AjaxSolr.Manager({
      solrUrl: 'http://' + window.location.hostname + ':8983/solr/'
    });
    Manager.addWidget(new AjaxSolr.ResultWidget({
          id: 'result',
          target: '#result'
    }));
    Manager.addWidget(new AjaxSolr.PagerWidget({
          id: 'pager',
          target: '#pager',
          prevLabel: '&lt;',
          nextLabel: '&gt;',
          innerWindow: 1,
          renderHeader: function (perPage, offset, total) {
            $('#pager-header').html($('<span/>').text('displaying ' + Math.min(total, offset + 1) + ' to ' + Math.min(total, offset + perPage) + ' of ' + total + ' results'));
          }
    }));
    
    var fields = [ 'emotion', 'artist' ];
    for (var i = 0, l = fields.length; i < l; i++) {
          Manager.addWidget(new AjaxSolr.TagCloudWidget({
            id: fields[i],
            target: '#' + fields[i],
            field: fields[i]
          }));
    }
    Manager.addWidget(new AjaxSolr.ImgCloudWidget({
        id: 'language',
        target: '#' + 'language',
        field: 'language'
    }));
    Manager.addWidget(new AjaxSolr.CurrentSearchWidget({
          id: 'currentsearch',
          target: '#selection',
    }));
    Manager.addWidget(new AjaxSolr.TextWidget({
          id: 'text',
          target: '#search',
          field: 'fullText'
    }));
    Manager.init();
    Manager.store.addByValue('q', '*:*');
    Manager.store.addByValue('rows', 3);
    var params = {
          facet: true,
          'facet.field': ['emotion', 'language', 'artist'],
          'facet.limit': 20,
          'facet.mincount': 1,
          'f.topics.facet.limit': 50,
          'json.nl': 'map',
          'hl' : true,
          'hl.fl' : '*',
          'hl.fragsize': 30,
          'hl.highlightMultiTerm' : true,
          'hl.simple.pre' : '<strong>',
          'hl.simple.post' : '</strong>'
    };
    for (var name in params) {
          Manager.store.addByValue(name, params[name]);
    }
    
    Manager.doRequest();
  });

})(jQuery);
