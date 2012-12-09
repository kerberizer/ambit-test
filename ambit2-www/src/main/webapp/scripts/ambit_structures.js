function defineStructuresTable(url, query_service, similarity) {
	var imgSize = 150;
	var oTable = $('#structures')
			.dataTable(
					{
						"aoColumnDefs" : [
								{ //0
									"aTargets": [ 0 ],	
									"sClass" : "center",
									"bSortable" : false,
									"mDataProp" : null,
									sWidth : "16px",
									"bUseRendered" : "true",
									"fnRender" : function(o,val) {
										return "<span class='zoomstruc'><img  src='"+query_service+"/images/zoom_in.png' alt='zoom in' title='Click to show compound details'></span>";
									}
								},						                  
								{ // 1
									"mDataProp" : "compound.URI",
									"aTargets" : [ 1 ],
									"sClass" : "center",
									"bSortable" : false,
									"bSearchable" : true,
									"bUseRendered" : false,
									sWidth : "32px",
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return "";
										else
											return "<input class='selecturi' type='checkbox' checked name='uri[]' title='Select "+ val +"' value='"+val+"'>\n";
									}
								},
								{
									"mDataProp" :null,
									"aTargets" : [ 2 ],
									"bSortable" : false,
									"bSearchable" : false,
									sWidth : "5%",
									"bVisible" : true
								},
								{
									"mDataProp" : "compound.cas",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 3 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : false,
									"sWidth" : "10%",
									"sClass" : "cas",
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData, "cas");
										else
											return val;
									},
									"bVisible" : true
								},			
								{
									"mDataProp" : "compound.URI",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 4 ],
									"bSearchable" : true,
									"bUseRendered" : false,
									"bSortable" : true,
									"sHeight" : imgSize+"px",
									"sWidth" : imgSize+"px",
									"fnRender" : function(o, val) {
										var cmpURI = val;
										if (val.indexOf("/conformer") >= 0) {
											cmpURI = val.substring(0, val
													.indexOf("/conformer"));
										}
										// if ((opentox["model_uri"]==null) ||
										// (opentox["model_uri"] == undefined))
										// {
										cmpURI = cmpURI + "?media=image/png";
										// } else {
										// cmpURI = opentox["model_uri"] +
										// "?dataset_uri=" + cmpURI +
										// "&media=image/png";
										// }
										/*
										 * "<a
										 * href=\"%s%s/%d?headless=true&details=false&media=text/html\"
										 * title=\"Molecule\">Molecule</a>",
										 */
										return '<img class="ui-widget-content" title="Structure diagram" border="0" src="'
												+ cmpURI + '&w='+imgSize+'&h='+imgSize+'">';
									}
								},
								{
									"mDataProp" : "compound.name",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 5 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : false,
									"sClass" : "names",
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData,
													"names");
										else
											return val;
									},
									"bVisible" : true
								},									
								{
									"mDataProp" : "compound.metric",
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 6 ],
									"sTitle" : "Similarity",
									"sClass" : "similarity",
									"bSearchable" : true,
									"bSortable" : true,
									"sWidth" : "5%",
									"bVisible" : similarity
								},
								{
									"mDataProp" : null,
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 7 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : true,
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData,
													"smiles");
										else
											return val;
									},
									"bVisible" : true
								},
								{
									"mDataProp" : null,
									"asSorting" : [ "asc", "desc" ],
									"aTargets" : [ 8 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : true,
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData,
													"inchi");
										else
											return val;
									},
									"bVisible" : false
								},
								{
									"mDataProp" : null,
									"asSorting" : [ "asc", "desc" ],
									"sClass" : "inchikey",
									"aTargets" : [ 9 ],
									"bSearchable" : true,
									"bSortable" : true,
									"bUseRendered" : true,
									"fnRender" : function(o, val) {
										if ((val === undefined) || (val == ""))
											return formatValues(o.aData,
													"inchikey");
										else
											return val;
									},
									"bVisible" : false
								} ],

						"bProcessing" : true,
						"bServerSide" : false,
						"bStateSave" : false,
						"bJQueryUI" : true,
						"bPaginate" : true,
						"sPaginate" : "dataTables_paginate paging_",
						"bDeferRender": true,
						"bSearchable": true,
						//"sDom" : 'R<"clear"><"fg-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix remove-bottom"ip>',
						"sAjaxSource": url,
						"sAjaxDataProp" : "dataEntry",
						"fnServerData" : function(sSource, aoData, fnCallback,
								oSettings) {
							
							oSettings.jqXHR = $.ajax({
								"type" : "GET",
								"url" : sSource,
								"data" : aoData,
								"dataType" : "json",
								/*
								 * useless - with datatype jsonp no custom headers are sent!
						        'beforeSend': function(xhrObj){
					                xhrObj.setRequestHeader("Content-Type","application/x-javascript");
					                xhrObj.setRequestHeader("Accept","application/x-javascript");
						        },								
								"headers": { 
								        "Accepts" : "application/x-javascript",
								        "Content-Type": "application/x-javascript"
								},						
								"accepts" : {
									jsonp: "application/x-javascript",
									json : "application/json"
								},
								*/
								"contentType" : "application/json",
								"success" : function(json) {
									try {
										$('#description').text(json['query']['summary']);
									} catch (err) { $('#description').text('');}
									identifiers(json);
									fnCallback(json);
								},
								"cache" : false,
								"error" : function(xhr, textStatus, error) {
									oSettings.oApi._fnProcessingDisplay(
											oSettings, false);
								}
							});
						},
						"oLanguage" : {
							"sProcessing" : "<img src='images/24x24_ambit.gif' border='0'>",
							"sLoadingRecords" : "No records found.",
							"sInfo": "Query results"  
						},
						"fnRowCallback" : function(nRow, aData, iDisplayIndex) {
			                $('td:eq(2)', nRow).html(iDisplayIndex +1);

			                // retrieve identifiers
							id_uri = query_service
									+ "/query/compound/url/all?search="
									+ encodeURIComponent(aData.compound.URI)
									+ "?max=1&media=application%2Fjson";
							$.ajax({
										dataType : "json",
										url : id_uri,
										success : function(data, status, xhr) {
											identifiers(data,aData);
											$.each(data.dataEntry,function(index,entry) {
												
														aData.compound.cas = formatValues(entry,"cas");																
														aData.compound.name = formatValues(entry,"names");																
														$('td:eq(5)',nRow).html(aData.compound.name);
														$('td:eq(3)',nRow).html(aData.compound.cas);
														aData.compound['smiles'] = formatValues(entry,"smiles");
														var offset = similarity?1:0;
														$('td:eq(' + (6 + offset) + ')',nRow)
																.html(aData.compound['smiles']);
														aData.compound['inchi'] = formatValues(entry,"inchi");
														$('td:eq(' + (7 + offset) + ')',nRow)
																.html(aData.compound['inchi']);
														aData.compound['inchikey'] = formatValues(
																entry,"inchikey");
														$('td:eq(' + (8 + offset) + ')',nRow)
																.html(aData.compound['inchikey']);
													});
											

										},
										error : function(xhr, status, err) {
										},
										complete : function(xhr, status) {
										}
									});

						}
					});
	
	$('#structures tbody td .zoomstruc img').live(
			'click',
			function() {
				var nTr = $(this).parents('tr')[0];
				if (oTable.fnIsOpen(nTr)) {
					this.src = query_service + "/images/zoom_in.png";
					this.alt = "Zoom in";
					this.title='Click to show compound details';
					oTable.fnClose(nTr);
				} else {
				    this.alt = "Zoom out";
					this.src = query_service + "/images/zoom_out.png";
					this.title='Click to close compound details panel';
					var id = 'values'+getID();
					oTable.fnOpen(nTr, fnFormatDetails(nTr,id),
							'details');
					
				       $('#'+ id).dataTable({
				    		'bJQueryUI': false, 
				    		'bPaginate': false,
				    		'bAutoWidth': true,
							"sScrollY": "200px",
							//"sScrollXInner": "110%",
							"bScrollCollapse": true,
							"sWidth": "90%",
				    		"sDom": 'T<"clear"><"fg-toolbar ui-helper-clearfix"lfr>t<"fg-toolbar ui-helper-clearfix"ip>',
				    		"aaSorting" : [ [ 0, 'desc' ] ],
				    		fnDrawCallback: function(){
				    			  var wrapper = this.parent();
				    			  var rowsPerPage = this.fnSettings()._iDisplayLength;
				    			  var rowsToShow = this.fnSettings().fnRecordsDisplay();
				    			  var minRowsPerPage = this.fnSettings().aLengthMenu[0][0];
				    			  if ( rowsToShow <= rowsPerPage || rowsPerPage == -1 ) {
				    			    $('.dataTables_paginate', wrapper).css('visibility', 'hidden');
				    			  }
				    			  else {
				    			    $('.dataTables_paginate', wrapper).css('visibility', 'visible');
				    			  }
				    			  if ( rowsToShow <= minRowsPerPage ) {
				    			    $('.dataTables_length', wrapper).css('visibility', 'hidden');
				    			  }
				    			  else {
				    			    $('.dataTables_length', wrapper).css('visibility', 'visible');
				    			  }
				    		}		
    		
				    	});								       
				}
			});
	
	function fnFormatDetails(nTr, id) {
		var dataEntry = oTable.fnGetData(nTr);
		var sOut = '<div class="ui-widget" style="margin-top: 5x;" >';
		sOut += '<div">';
		sOut += '<table width="100%"><tbody>';//outer table, can't get the style right with divs
		$.each(dataEntry.lookup.cas, function(k, value) {
			sOut += renderValue("CAS RN",dataEntry.values[value]);
		});
		$.each(dataEntry.lookup.einecs, function(k, value) {
			sOut += renderValue("EC",dataEntry.values[value]);
		});	
		$.each(dataEntry.lookup.inchi, function(k, value) {
			sOut += renderValue("InChI",dataEntry.values[value]);
		});		
		$.each(dataEntry.lookup.inchikey, function(k, value) {
			sOut += renderValue("InChIKey",dataEntry.values[value]);
		});
		$.each(dataEntry.lookup.names, function(k, value) {
			sOut += renderValue("Name",dataEntry.values[value]);
		});
		$.each(dataEntry.lookup.reachdate, function(k, value) {
			sOut += renderValue("REACH date",dataEntry.values[value]);
		});
		$.each(dataEntry.lookup.smiles, function(k, value) {
			sOut += renderValue("SMILES",dataEntry.values[value]);
		});
		$.each(dataEntry.lookup.misc, function(k, value) {
			sOut += renderValue(value,dataEntry.values[value]);
		});
		sOut += '</tbody></table></div></div>\n';		
		return sOut;
	}
	
	function renderValue(feature, value) {
		if (value != undefined) {
			var sOut = "<tr><th>" + feature + "</th><td>" + value + "</td></tr>";
			return sOut;
		} else return "";
	}
	return oTable;
}




function getID() {
	return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
}

function formatValues(dataEntry, tag) {
	var sOut = "";
	var delimiter = "";
	$.each(dataEntry.lookup[tag], function(index, value) {
		if (dataEntry.values[value] != undefined) {
			$.each(dataEntry.values[value].split("|"), function(index, v) {
				if (v.indexOf(".mol") == -1) {
					if ("" != v) {
						sOut += delimiter;
						sOut += v;
						delimiter = "<br>";
					}
				}
			});
			// sOut += dataEntry.values[value];
		}
	});
	return sOut;
}

function selecturi(value) {
	$('.selecturi').prop('checked', value);
}
