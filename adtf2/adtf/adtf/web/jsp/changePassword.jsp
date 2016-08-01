
<label class="changepw" onclick="showdiv(this,'block','pwform');">ChangePassword</label>

<div id="pwform" style="display: none; background-color: #ffffff"
	class="PwBoX">
	<form action="#" onsubmit="return changepass();">

		<table border="0" cellpadding="3" cellspacing="0" width="320">

			<tr>
				<td id="menuhead">
					<div id="msg"></div>
				</td>

				<td><img
					src="<%=request.getContextPath()%>/images/but_close.gif"
					alt="CLOSE" style="cursor: pointer;"
					onclick="showdiv(this,'none','pwform');" align="right" border="0"
					height="13" width="13"></td>
			</tr>
			<tr>
				<td><b>New Password :</b></td>
				<td><input class="textfield" id="newpw" type="password" /> <input
					type="hidden"
					value="<auth:authentication property="principal.username" />" /></td>
			</tr>
			<tr>
				<td><b> Confirm Password : </b></td>
				<td><input class="textfield" id="cpw" type="password" /></td>
			</tr>

			<tr>
				<td><input type="submit" value="SUBMIT" class="loginbtn1" /></td>
				<td><input type="reset" value="RESET" class="loginbtn1" /></td>
			</tr>

		</table>
	</form>
</div>