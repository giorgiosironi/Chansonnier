(function ($) {

AjaxSolr.theme.prototype.result = function (doc, snippet) {
  var output = '<div><h2>' + doc.artist + ' - ' + doc.title + '</h2>';
  output += '<p class="lyrics">' + doc.lyrics + '</p>';
  output += '<p id="links_' + doc.uuid + '" class="links"></p>';
  //output += '<p>' + snippet + '</p>';
  image = function(attachmentName) {
  	var path = 'attachment?id=' + doc.link + '&name=' + attachmentName;
  	return '<a href="' + path + '"><img src="' + path + '" height="30" width="40" /></a>';
  }
  output += '<div class="images">';
  for (index in doc.image) {
  	output += image(doc.image[index]);
  }
  output += '</div>';
  return output;
};

AjaxSolr.theme.prototype.tag = function (value, weight, handler) {
  return $('<a href="#" class="tagcloud_item"/>').text(value).addClass('tagcloud_size_' + weight).click(handler);
};

AjaxSolr.theme.prototype.facet_link = function (value, handler) {
  return $('<a href="#"/>').text(value).click(handler);
};

AjaxSolr.theme.prototype.no_items_found = function () {
  return 'no items found in current selection';
};

})(jQuery);