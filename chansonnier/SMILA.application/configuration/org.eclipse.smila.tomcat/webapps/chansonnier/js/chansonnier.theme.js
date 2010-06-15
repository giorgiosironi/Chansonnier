(function ($) {

AjaxSolr.theme.prototype.result = function (doc, snippet) {
  var output = '<div><h2>' + doc.Artist + ' - ' + doc.Title + '</h2>';
  output += '<p class="lyrics">' + doc.Lyrics + '</p>';
  output += '<p id="links_' + doc.uuid + '" class="links"></p>';
  //output += '<p>' + snippet + '</p>';
  image = function(attachmentName) {
  console.log(doc);
  	var path = 'http://localhost:8080/chansonnier/attachment?id=' + doc.link + '&name=' + attachmentName;
  	//var path = 'attachment?id=' + doc.link + '&name=' + attachmentName;
  	return '<a href="' + path + '"><img src="' + path + '" height="30" width="40" /></a>';
  }
  output += '<div class="images">' + image('Image1') + image('Image2') + image('Image3') + '</div>';
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