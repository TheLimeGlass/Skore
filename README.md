# Skore
A Skript addon dedicated to perfecting scoreboards.

Set `use-config` to false in the TitleManager configurations to be able to fully utilize TitleManager and Skore's syntax.

Example:
```
on join:
	setup skoreboard for player
	if player has a skoreboard:
		set title of skoreboard player to "&a&lDamn, this some clean title"
		set slot 1 of skoreboard player to "test"
```