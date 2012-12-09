<!-- thirteen-->
<div class="thirteen columns" id="query">
<form method='GET' name='searchform' id='searchform' method='get' action='${ambit_root}/ui'>
<div class="seven columns alpha">
	<h3 class="remove-bottom">
			Structure search	
	</h3>
	<h6 class="remove-bottom">
	<input type='radio' checked value='auto' name='option' title='Exact structure or search by an identifier. CAS, Chemical name, SMILES or InChI. The input type is guessed automatically.' size='20' onClick='clickAuto();'>Exact structure or identifier
	<input type='radio' name='option' value='similarity' title='Enter SMILES or draw structure' onClick='clickSimilarity();'>Similarity&nbsp;
	<input type='radio' name='option' value='smarts' title='Enter or draw a SMARTS query' size='20' onClick='clickSmarts();'>Substructure&nbsp;
	<input type='hidden' name='type' value='smiles'>	
	<br>
	<span class='help' id='strucSearch'></span>
	</h6>
</div>
<div class="four columns omega">
	<h3 class="remove-bottom">
		&nbsp;
	</h3>
	<h6 class="remove-bottom">	
	<span id='thresholdSpan' style='display:none;' class="remove-bottom">Threshold:
	<select title ='Tanimoto similarity threshold' id='threshold' name='threshold' style='width:6em;' class="remove-bottom" >
			<option value='0.9' selected>0.9</option>
	   		<option value='0.8' >0.8</option>
	   		<option value='0.7' >0.7</option>
	   		<option value='0.6' >0.6</option>
	   		<option value='0.5' >0.5</option>
	   		<option value='0.4' >0.4</option>
	   		<option value='0.3' >0.3</option>
	   		<option value='0.2' >0.2</option>
	   		<option value='0.1' >0.1</option>
	</select>	
	</span>
	<span id='funcgroupsSpan' style='display:none;' class="remove-bottom">Functional groups:
    <select size='1' title ='Predefined functional groups' id='funcgroups' name='funcgroups'>
	</select>
	</span>
	</h6>
</div>
<div class="three columns omega">
	<h3 class="remove-bottom">
		&nbsp;
	</h3>
	<h6 class="remove-bottom">
		<input type='text' id='search' name='search' size='40' value='' tabindex='1'>
		<input type='hidden' size='3' name='pagesize' value='10'>
	</h6>	
</div>
<div class="one column omega">
	<h3 class="remove-bottom">
		&nbsp;
	</h3>
	<h6 class="remove-bottom">
		<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'/>
	</h6>
</div>		
</form>
</div>	
