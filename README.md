# Skore
A Skript addon dedicated to perfecting scoreboards.

Requires ProtocolLib https://www.spigotmc.org/resources/protocollib.1997/

Example:
```
on join:
	setup skoreboard for player
	if player has a skoreboard:
		set title of skoreboard player to "&a&lDamn, this some clean title"
		set slot 1 of skoreboard player to "test"
```

Syntax:
```
Syntax:
  PropertyExpressions:
    ExprValue:
      enabled: true
      changers: '[SET, DELETE, RESET]'
      description: Returns or changes the value of the Skoreboard(s).
      syntax:
      - '[Skore] [(all [[of] the]|the)] (slot|value|line)[s] %numbers% (of|from|in)
        skoreboard[s] %players%'
      - '[Skore] %players%''[s] skoreboard[s] (slot|value|line)[s] %numbers%'
    ExprTitle:
      enabled: true
      changers: '[SET, DELETE, RESET]'
      description: Returns or changes the title of the Skoreboard(s).
      syntax:
      - '[Skore] [(all [[of] the]|the)] title[s] (of|from|in) skoreboard[s] %players%'
      - '[Skore] %players%''[s] skoreboard[s] title[s]'
  Conditions:
    CondPlayerSkoreboard:
      enabled: true
      description: Check if the player has a skoreboard initialized.
      syntax:
      - '%player% (1¦has|2¦does not have) [a] skoreboard'
  Effects:
    EffSetupSkoreboard:
      enabled: true
      description: Setup the Skoreboard for the player, tells the system their skoreboard.
      syntax:
      - (1¦(create|set[up])|2¦(remove|reset|delete)) [a] [([skore] sc|sk)oreboard]
        (for|to|of) %players%
```
