(function ($) {
AjaxSolr.CurrentSearchWidget = AjaxSolr.AbstractWidget.extend({
	defaultQuery : '*:*',
	afterRequest: function () {
  		var self = this;
  		var links = [];
  		
  		var q = this.manager.store.values('q');
  		if (q != self.defaultQuery) {
  			links.push($('<a href="#"/>').text('(x) ' + q).click(self.resetQuery()));
  		}

		var fq = this.manager.store.values('fq');
  		for (var i = 0, l = fq.length; i < l; i++) {
    		links.push($('<a href="#"/>').text('(x) ' + fq[i]).click(self.removeFacet(fq[i])));
  		}

  		if (links.length) {
    		AjaxSolr.theme('list_items', this.target, links);
  		} else {
    		$(this.target).html('<div>Viewing all songs!</div>');
  		}
	},
	resetQuery : function () {
		var self = this;
		return function () {
    		self.manager.store.get('q').val(self.defaultQuery);
	      	self.manager.doRequest(0);
    		return false;
  		};
	},
	removeFacet: function (facet) {
  		var self = this;
  		return function () {
    		if (self.manager.store.removeByValue('fq', facet)) {
	      		self.manager.doRequest(0);
    		}
    		return false;
  		};
	}
});
})(jQuery);
