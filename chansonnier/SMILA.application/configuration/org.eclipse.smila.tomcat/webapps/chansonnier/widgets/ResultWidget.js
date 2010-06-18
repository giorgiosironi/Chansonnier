(function ($) {

AjaxSolr.ResultWidget = AjaxSolr.AbstractWidget.extend({
  afterRequest: function () {
    $(this.target).empty();
    for (var i = 0, l = this.manager.response.response.docs.length; i < l; i++) {
      var doc = this.manager.response.response.docs[i];
      $(this.target).append(AjaxSolr.theme('result', doc));        
      var items = this.facetLinks('emotion', [doc.emotion]);
      var items = this.facetLinks('language', [doc.language]);
      AjaxSolr.theme('list_items', '#links_' + doc.uuid, items);
    }
    $('.images a').lightBox();
  },
  facetLinks: function (facet_field, facet_values) {
  	var links = new Array();
  	if (facet_values) {
    	for (var i = 0, l = facet_values.length; i < l; i++) {
    		links = links.concat(AjaxSolr.theme('facet_link', facet_values[i], this.facetHandler(facet_field, facet_values[i])));
    	}
  	}
  	return links;
  },
  facetHandler: function (facet_field, facet_value) {
  	var self = this;
  	return function () {
  	  self.manager.store.remove('fq');
  	  self.manager.store.addByValue('fq', facet_field + ':' + facet_value);
  	  self.manager.doRequest(0);
  	  return false;
  	};
  }
});

})(jQuery);
