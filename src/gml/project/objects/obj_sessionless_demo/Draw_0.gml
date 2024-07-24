var offset_y = y;

draw_text(x, offset_y, "Press [N] to create new random Sessionless context.");
offset_y += y_new_line;
draw_text(x, offset_y, "Press [B] to build new Sessionless context.");
offset_y += y_new_line;
draw_text(x, offset_y, "Press [I] to insert new message.");
offset_y += y_new_section;

draw_text(x, offset_y, string("Sessionless context ID: {0}", ctx.id));
offset_y += y_new_line;
draw_text(x, offset_y, string("Sessionless private key: {0}", private_key));
offset_y += y_new_line;
draw_text(x, offset_y, string("Sessionless public key: {0}", public_key));
offset_y += y_new_section;

draw_text(x, offset_y, string("Message: {0}", message));
offset_y += y_new_line;
draw_text(x, offset_y, string("Signature: {0}", signature));
offset_y += y_new_line;
draw_text(x, offset_y, string("Is verified: {0}", is_verified));
offset_y += y_new_line;
