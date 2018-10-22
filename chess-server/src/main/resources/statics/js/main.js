/** chines chess
*	Author:	fdipzone
*	Date:	2012-06-24
*	Ver:	1.0
*/

var gameimg = [
               '/statics/images/a1.gif','/statics/images/a2.gif',
               '/statics/images/a3.gif','/statics/images/a4.gif',
               '/statics/images/a5.gif','/statics/images/a6.gif',
               '/statics/images/a7.gif','/statics/images/b1.gif',
               '/statics/images/b2.gif','/statics/images/b3.gif',
               '/statics/images/b4.gif','/statics/images/b5.gif',
               '/statics/images/b6.gif','/statics/images/b7.gif',
               '/statics/images/bg.gif','/statics/images/bg_over.gif',
               '/statics/images/bg_sel.gif'];
var chess_obj = new ChessClass();
var so

window.onload = function(){

	var userId = prompt("请输入您的ID?","请在这里输入ID");
	Socket.prototype.action_200 = function(msg){
		if(so != null){
			console.info(msg);
			if(msg && !msg.response.data){
				var nickname = prompt("请输入您的昵称?");
				so.sendMessage({"msgId":"10002","params":{"nickname":nickname}});
			}else{
				so.sendMessage({"msgId":"10002","params":{"nickname":""}});
			}
		}
	}
	Socket.prototype.action_10002 = function(msg){
		if(msg.response.errcode == 0){
			$("#log").append("<br/>"+msg.from+"登录成功");
			console.info(msg.from);
			if(userId == msg.from){
				so.sendMessage({"msgId":"20002"});
			}
			return;
		}
		$("#log").append("<br/>"+msg.from+"登录失败");
	}
	Socket.prototype.action_20001 = function(msg){
		if(msg.response.errcode == 0){
			$("#log").append("<br/>"+"创建房间"+JSON.stringify(msg));
			so.sendMessage({"msgId":"20002"});
			return;
		}
	}
	Socket.prototype.action_20002 = function(msg){
		if(msg.response.errcode == 0){
			$("#log").append("<br/>"+"获取房间号"+JSON.stringify(msg));
			if(!msg.response.data){
				so.sendMessage({"msgId":"20001"});
				return;
			}
			if(userId == msg.from){
				var room = msg.response.data;
				so.sendMessage({"msgId":"20003","params":{"roomId":room.id}});
			}
		}
	}
	Socket.prototype.action_20003 = function(msg){
		console.info("20003"+JSON.stringify(msg));
		if(msg.response.errcode == 0){
			$("#log").append("<br/>"+"进入房间"+JSON.stringify(msg));
		}
	}
	Socket.prototype.action_20004 = function(msg){
		console.info("20004"+JSON.stringify(msg));
		if(msg.response.errcode == 0){
			$("#log").append("<br/>"+msg.from+"退出房间");
		}
	}
	
	Socket.prototype.action_30001 = function(msg){
		console.info("30001"+JSON.stringify(msg));
		//init
		chess_obj.init();
	}
	Socket.prototype.action_30002 = function(msg){
		console.info("30002"+JSON.stringify(msg));
		//重绘
		chess_obj.repaint(msg.response.data);
	}
	
	so = new Socket(userId);
	so.connection();
		
	$ID('init_btn').onclick = function(){
		chess_obj.init();
	}
	var callback = function(){
		chess_obj.init();
	}
	img_preload(gameimg, callback);
}


// chess class
function ChessClass(){
	this.chess = [];
	this.boardrows = 4;
	this.boardcols = 8;
	this.area = 82;
	this.player = 1;		// 1:red 2:green
	this.selected = null;	// selected chess
	this.chesstype = ['', 'a', 'b'];
	this.isover = 0;
}


// init
ChessClass.prototype.init = function(sc){
	//var sc = JSON.parse('{"red":15,"green":16,"player":3,"isover":0,"chess_all":[{"chess":"a7","type":"a","val":"7","status":-1},{"chess":"a1","type":"a","val":"1","status":1},{"chess":"b5","type":"b","val":"5","status":1},{"chess":"a7","type":"a","val":"7","status":1},{"chess":"a7","type":"a","val":"7","status":1},{"chess":"b7","type":"b","val":"7","status":0},{"chess":"b7","type":"b","val":"7","status":0},{"chess":"b1","type":"b","val":"1","status":0},{"chess":"a6","type":"a","val":"6","status":0},{"chess":"b7","type":"b","val":"7","status":0},{"chess":"a7","type":"a","val":"7","status":0},{"chess":"a2","type":"a","val":"2","status":1},{"chess":"b6","type":"b","val":"6","status":1},{"chess":"a2","type":"a","val":"2","status":0},{"chess":"a5","type":"a","val":"5","status":0},{"chess":"b6","type":"b","val":"6","status":0},{"chess":"a7","type":"a","val":"7","status":0},{"chess":"a3","type":"a","val":"3","status":0},{"chess":"a4","type":"a","val":"4","status":0},{"chess":"a4","type":"a","val":"4","status":1},{"chess":"b7","type":"b","val":"7","status":1},{"chess":"a6","type":"a","val":"6","status":0},{"chess":"b5","type":"b","val":"5","status":0},{"chess":"b3","type":"b","val":"3","status":0},{"chess":"a3","type":"a","val":"3","status":0},{"chess":"b3","type":"b","val":"3","status":0},{"chess":"b4","type":"b","val":"4","status":1},{"chess":"b4","type":"b","val":"4","status":0},{"chess":"a5","type":"a","val":"5","status":0},{"chess":"b2","type":"b","val":"2","status":0},{"chess":"b7","type":"b","val":"7","status":0},{"chess":"b2","type":"b","val":"2","status":0}]}');
	
	this.reset_grade();	
	this.create_board();
	if(sc){
		this.chess = sc.chess_all;
		this.player = sc.player;
		$ID('grade_num1').innerHTML = sc.red;
		$ID('grade_num2').innerHTML = sc.green;
		$ID('grade_img1').className = $ID('grade_img2').className = 'img';
		if($ID('grade_img'+this.player))
			$ID('grade_img'+this.player).className = 'img_on';
		this.isover = sc.isover;
		if(this.isover == 1){
			this.show_grade();
			disp('init_div','show');
		}else{
			var chess = this.chess;
			for(var i=0,max=chess.length; i<max; i++){
				if(chess[i].status == 1){
					$ID(this.getid(i)).innerHTML = '<img src="/statics/images/' + chess[i]['chess'] + '.gif" />';
				}else if(chess[i].status == -1){
					var id = this.getid(i);
					$ID(id).innerHTML = '';
					$ID(id).className = '';
				}
			}
		}
	}else{
		this.create_chess();
		this.player = 1;
		this.selected = null;
		this.isover = 0;
		disp('init_div','hide');
	}
	this.create_event();
}


ChessClass.prototype.repaint = function(sc){
	//var sc = JSON.parse('{"red":15,"green":16,"player":3,"isover":0,"chess_all":[{"chess":"a7","type":"a","val":"7","status":-1},{"chess":"a1","type":"a","val":"1","status":1},{"chess":"b5","type":"b","val":"5","status":1},{"chess":"a7","type":"a","val":"7","status":1},{"chess":"a7","type":"a","val":"7","status":1},{"chess":"b7","type":"b","val":"7","status":0},{"chess":"b7","type":"b","val":"7","status":0},{"chess":"b1","type":"b","val":"1","status":0},{"chess":"a6","type":"a","val":"6","status":0},{"chess":"b7","type":"b","val":"7","status":0},{"chess":"a7","type":"a","val":"7","status":0},{"chess":"a2","type":"a","val":"2","status":1},{"chess":"b6","type":"b","val":"6","status":1},{"chess":"a2","type":"a","val":"2","status":0},{"chess":"a5","type":"a","val":"5","status":0},{"chess":"b6","type":"b","val":"6","status":0},{"chess":"a7","type":"a","val":"7","status":0},{"chess":"a3","type":"a","val":"3","status":0},{"chess":"a4","type":"a","val":"4","status":0},{"chess":"a4","type":"a","val":"4","status":1},{"chess":"b7","type":"b","val":"7","status":1},{"chess":"a6","type":"a","val":"6","status":0},{"chess":"b5","type":"b","val":"5","status":0},{"chess":"b3","type":"b","val":"3","status":0},{"chess":"a3","type":"a","val":"3","status":0},{"chess":"b3","type":"b","val":"3","status":0},{"chess":"b4","type":"b","val":"4","status":1},{"chess":"b4","type":"b","val":"4","status":0},{"chess":"a5","type":"a","val":"5","status":0},{"chess":"b2","type":"b","val":"2","status":0},{"chess":"b7","type":"b","val":"7","status":0},{"chess":"b2","type":"b","val":"2","status":0}]}');
	
	//this.reset_grade();	
	//this.create_board();
	if(sc){
		this.chess = sc.chess_all;
		this.player = sc.player;
		$ID('grade_num1').innerHTML = sc.red;
		$ID('grade_num2').innerHTML = sc.green;
		$ID('grade_img1').className = $ID('grade_img2').className = 'img';
		if($ID('grade_img'+this.player))
			$ID('grade_img'+this.player).className = 'img_on';
		this.isover = sc.isover;
		if(this.isover == 1){
			this.show_grade();
			disp('init_div','show');
		}else{
			var chess = this.chess;
			for(var i=0,max=chess.length; i<max; i++){
				if(chess[i].status == 1){
					$ID(this.getid(i)).innerHTML = '<img src="/statics/images/' + chess[i]['chess'] + '.gif" />';
				}else if(chess[i].status == -1){
					var id = this.getid(i);
					$ID(id).innerHTML = '';
					$ID(id).className = '';
				}
			}
		}
	}
}


// create board
ChessClass.prototype.create_board = function(){
	var board = '';
	for(var i=0; i<this.boardrows; i++){
		for(var j=0; j<this.boardcols; j++){
			board = board + '<div id="' + i + '_' + j + '"><img src="/statics/images/chessbg.gif" /></div>';
		}
	}
	$ID('board').innerHTML = board;
	$ID('board').style.width = this.boardcols * (this.area + 2) + 'px';
	$ID('board').style.height = this.boardrows * (this.area + 2) + 'px';
}


// create random chess
ChessClass.prototype.create_chess = function(){
	// 32 chesses
	var chesses = ['a1','b7','a2','b7','a2','b7','a3','b7','a3','b7','a4','b6','a4','b6','a5','b5','a5','b5','a6','b4','a6','b4','a7','b3','a7','b3','a7','b2','a7','b2','a7','b1'];
	this.chess = [];
	while(chesses.length>0){
		var rnd = Math.floor(Math.random()*chesses.length);
		var tmpchess = chesses.splice(rnd, 1).toString();
		this.chess.push({'chess':tmpchess, 'type':tmpchess.substr(0,1), 'val':tmpchess.substr(1,1), 'status':0});
	}
}


// create event
ChessClass.prototype.create_event = function(){
	var self = this;
	var chess_area = $ID_tag('div', 'board');
	for(var i=0; i<chess_area.length; i++){
		chess_area[i].onmouseover = function(){	// mouseover
			if(this.className!='onsel'){
				this.className = 'on';
			}
		}
		chess_area[i].onmouseout = function(){	// mouseout
			if(this.className!='onsel'){
				this.className = '';
			}
		}
		chess_area[i].onclick = function(){	// onclick
			self.action(this);
		}
	}
}


// id change index
ChessClass.prototype.getindex = function(id){
	var tid = id.split('_');
	return parseInt(tid[0])*this.boardcols + parseInt(tid[1]);
}


// index change id
ChessClass.prototype.getid = function(index){
	return parseInt(index/this.boardcols) + '_' + parseInt(index%this.boardcols);
}


// action
ChessClass.prototype.action = function(o){
	if(this.isover==1){	// game over
		return false;
	}
	
	var index = this.getindex(o.id);

	if(this.selected == null){	// 未选过棋子
		if(this.chess[index]['status'] == 0){	// not opened
			this.show(index);	
		}else if(this.chess[index]['status'] == 1){	// opened
			if(this.chess[index]['type'] == this.chesstype[this.player]){
				this.select(index);
			}
		}		
	}else{	// 已选过棋子
		if(index != this.selected['index']){				// 與selected不是同一位置
			if(this.chess[index]['status'] == 0){			// 未打开的棋子
				this.show(index);
			}else if(this.chess[index]['status'] == -1){	// 點空白位置
				this.move(index);
			}else{											// 點其他棋子
				if(this.chess[index]['type']==this.chesstype[this.player]){
					this.select(index);
				}else{			
					this.kill(index);
				}
			}
		}
	}
}


// show chess
ChessClass.prototype.show = function(index){
	$ID(this.getid(index)).innerHTML = '<img src="/statics/images/' + this.chess[index]['chess'] + '.gif" />';
	this.chess[index]['status'] = 1;	// opened
	if(this.selected!=null){			// 清空選中
		$ID(this.getid(this.selected.index)).className = '';
		this.selected = null;
	}	
	this.change_player();
	this.gameover();
}


// select chess
ChessClass.prototype.select = function(index){
	if(this.selected!=null){
		$ID(this.getid(this.selected['index'])).className = '';
	}
	this.selected = {'index':index, 'chess':this.chess[index]};
	$ID(this.getid(index)).className = 'onsel';
}


// move chess
ChessClass.prototype.move = function(index){
	if(this.beside(index)){
		this.chess[index] = {'chess':this.selected['chess']['chess'], 'type':this.selected['chess']['type'], 'val':this.selected['chess']['val'], 'status':this.selected['chess']['status']};
		this.remove(this.selected['index']);
		this.show(index);
	}
}


// kill chess
ChessClass.prototype.kill = function(index){
	if(this.beside(index)==true && this.can_kill(index)==true){
		this.chess[index] = {'chess':this.selected['chess']['chess'], 'type':this.selected['chess']['type'], 'val':this.selected['chess']['val'], 'status':this.selected['chess']['status']};
		this.remove(this.selected['index']);
		var killed = this.player==1? 2 : 1;
		$ID('grade_num' + killed).innerHTML = parseInt($ID('grade_num' + killed).innerHTML)-1;	
		this.show(index);
	}
}


// remove chess
ChessClass.prototype.remove = function(index){
	this.chess[index]['status'] = -1;	// empty
	$ID(this.getid(index)).innerHTML = '';
	$ID(this.getid(index)).className = '';
}


/* check is beside
* @param index		目標棋子index
* @param selindex	执行的棋子index，可为空, 为空则读取选中的棋子
*/
ChessClass.prototype.beside = function(index,selindex){
	if(typeof(selindex)=='undefined'){
		if(this.selected!=null){
			selindex = this.selected['index'];
		}else{
			return false;
		}
	}

	if(typeof(this.chess[index])=='undefined'){
		return false;
	}

	var from_info = this.getid(selindex).split('_');
	var to_info = this.getid(index).split('_');
	var fw = parseInt(from_info[0]);
	var fc = parseInt(from_info[1]);
	var tw = parseInt(to_info[0]);
	var tc = parseInt(to_info[1]);

	if(fw==tw && Math.abs(fc-tc)==1 || fc==tc && Math.abs(fw-tw)==1){	// row or colunm is same and interval=1
		return true;
	}else{
		return false;
	}
}


/* check can kill
* @param index		被消灭的棋子index
* @param selindex	执行消灭的棋子index，可为空, 为空则读取选中的棋子
*/
ChessClass.prototype.can_kill = function(index,selindex){
	if(typeof(selindex)=='undefined'){	// 没有指定执行消灭的棋子
		if(this.selected!=null){		// 有选中的棋子
			selindex = this.selected['index'];
		}else{
			return false;
		}
	}
	if(this.chess[index]['type']!=this.chesstype[this.player]){
		if(parseInt(this.chess[selindex]['val'])==7 && parseInt(this.chess[index]['val'])==1){	// 7 can kill 1
			return true;
		}else if(parseInt(this.chess[selindex]['val'])==1 && parseInt(this.chess[index]['val'])==7){ // 1 can't kill 7
			return false;
		}else if(parseInt(this.chess[selindex]['val']) <= parseInt(this.chess[index]['val'])){	// small kill big
			return true;
		}
	}
	return false;
}


// change player
ChessClass.prototype.change_player = function(){
	if(this.player == 1){
		this.player = 2;	// to green
		$ID('grade_img2').className = 'img_on';
		$ID('grade_img1').className = 'img';
	}else{
		this.player = 1;	// to red
		$ID('grade_img1').className = 'img_on';
		$ID('grade_img2').className = 'img';
	}
}


// reset grade
ChessClass.prototype.reset_grade = function(){
	$ID('grade_img1').className = 'img_on';
	$ID('grade_img2').className = 'img';
	$ID('grade_num1').innerHTML = $ID('grade_num2').innerHTML = 16;
	$ID('grade_res1').className = $ID('grade_res2').className = 'none';
	$ID('grade_res1').innerHTML = $ID('grade_res2').innerHTML = '';
}


// game over
ChessClass.prototype.gameover = function(){
	var num1 = parseInt($ID('grade_num1').innerHTML);
	var num2 = parseInt($ID('grade_num2').innerHTML);
	var can;
	if(num1 == 0 || num2 == 0){	// 任一方棋子为0
		this.isover = 1;
		this.show_grade();
		disp('init_div','show');
	}else{
		can = this.can_action(); 
		if(can == false){
			this.isover = 1;
			this.show_grade();
			disp('init_div','show');
		}
	}
	console.log('Broadcast'+can+':reb['+num1+'] VS green['+num2+']'+this.player);
	
	so.sendMessage({"msgId":"30002","params":{"red":num1, "green":num2, "player":this.player, "isover":this.isover, "chess_all":this.chess}});
}


// show grade
ChessClass.prototype.show_grade = function(){
	var num1 = parseInt($ID('grade_num1').innerHTML);
	var num2 = parseInt($ID('grade_num2').innerHTML);
	if(num1>num2){ // 红方胜
		$ID('grade_res2').innerHTML = 'LOSS';
		$ID('grade_res2').className = 'loss';
		$ID('grade_res1').innerHTML = 'WIN';
		$ID('grade_res1').className = 'win';
	}else if(num1<num2){ // 黑方胜
		$ID('grade_res1').innerHTML = 'LOSS';
		$ID('grade_res1').className = 'loss';
		$ID('grade_res2').innerHTML = 'WIN';
		$ID('grade_res2').className = 'win';
	}else{	// 平局
		$ID('grade_res1').innerHTML = $ID('grade_res2').innerHTML = 'DRAW';
		$ID('grade_res1').className = $ID('grade_res2').className = 'draw';
	}
}


// check chess can action
ChessClass.prototype.can_action = function(){
	var chess = this.chess;
	for(var i=0,max=chess.length; i<max; i++){
		if(chess[i].status==0){	// 有未翻开的棋子
			return true;
		}else{
			if(chess[i].status==1 && chess[i].type==this.chesstype[this.player]){	// 己方已翻开的棋子
				if(this.beside(i-this.boardcols, i) && (chess[i-this.boardcols].status==-1 || this.can_kill(i-this.boardcols, i) )){	// 上
					return true;
				}
				if(this.beside(i+this.boardcols, i) && (chess[i+this.boardcols].status==-1 || this.can_kill(i+this.boardcols, i) )){	// 下
					return true;
				}
				if(this.beside(i-1, i) && (chess[i-1].status==-1 || this.can_kill(i-1,i) )){	// 左
					return true;
				}
				if(this.beside(i+1, i) && (chess[i+1].status==-1 || this.can_kill(i+1,i) )){	// 右
					return true;
				}
			}
		}
	}
	return false;
}


/** common function */

// get document.getElementBy(id)
function $ID(id){
	this.id = id;
	return document.getElementById(id);
}


// get document.getElementsByTagName
function $ID_tag(name, id){
	if(typeof(id)!='undefined'){
		return $ID(id).getElementsByTagName(name);
	}else{
		return document.getElementsByTagName(name);	
	}
}


/* div show and hide
* @param id dom id
* @param handle show or hide
*/
function disp(id, handle){
	if(handle=='show'){
		$ID(id).style.display = 'block';
	}else{
		$ID(id).style.display = 'none';	
	}
}


/* img preload
* @param img		要加载的图片数组
* @param callback	图片加载成功后回调方法
*/
function img_preload(img, callback){
	var onload_img = 0;
	var tmp_img = [];
	for(var i=0,imgnum=img.length; i<imgnum; i++){
		tmp_img[i] = new Image();
		tmp_img[i].src = img[i];
		if(tmp_img[i].complete){
			onload_img ++;
		}else{
			tmp_img[i].onload = function(){
				onload_img ++;
			}
		}
	}
	var et = setInterval(
		function(){
			if(onload_img==img.length){	// 定时器,判断图片完全加载后调用callback
				clearInterval(et);
				callback();
			}
		},200);
}