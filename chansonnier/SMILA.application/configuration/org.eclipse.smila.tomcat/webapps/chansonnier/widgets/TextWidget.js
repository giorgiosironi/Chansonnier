(function ($) {
AjaxSolr.TextWidget = AjaxSolr.AbstractTextWidget.extend({
	afterRequest: function () {
  		$(this.target).find('input').val('');

		var self = this;
  		$(this.target).find('input').bind('keydown', function(e) {
    		if (e.which == 13) {
      			var value = $(this).val();
      			if (value && self.set(self.field + ':"' + value + '"')) {
	        		self.manager.doRequest(0);
      			}
    		}
  		});
	}
});
})(jQuery);
