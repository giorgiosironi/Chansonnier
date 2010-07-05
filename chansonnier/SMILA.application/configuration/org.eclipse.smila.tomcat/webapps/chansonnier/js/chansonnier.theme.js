(function ($) {

AjaxSolr.theme.prototype.result = function (doc, snippets) {
  var output = '<div class="song"><h2><a href="' + doc.link + '">' + doc.artist + ' - ' + doc.title + '</a></h2>';
  output += '<p class="lyrics">' + doc.lyrics + '</p>';
  output += '<p id="links_' + doc.uuid + '" class="links"></p>';
  for (var index in snippets) {
  	output += '<p class="match">Match: ...' + snippets[index] + '... Score: ' + doc.score + '</p>';
  }
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

AjaxSolr.theme.prototype.img = function (value, weight, handler) {
  return $('<a href="#" class="tagcloud_item"/>').html('<img src="images/flags/' + value + '.gif" alt="' + value + '" /><span>' + value + '</span>').addClass('tagcloud_size_' + weight).click(handler);
};

AjaxSolr.theme.prototype.facet_link = function (value, handler) {
  return $('<a href="#"/>').text(value).click(handler);
};

AjaxSolr.theme.prototype.no_items_found = function () {
  return 'no songs found in current selection';
};

})(jQuery);