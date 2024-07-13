
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Pokemon {

    String name;
    String type1;
    String type2;
    int health;
    int maxHealth;
    int attack;
    int defense;
    int specialAttack;
    int specialDefense;
    int speed;
    int level;
    int experience;
    List<Move> moves;
    boolean isWild;
    String statusEffect;
    int statusDuration;

    public Pokemon(String name, String type1, String type2, int health, int attack, int defense, int specialAttack, int specialDefense, int speed, int level, boolean isWild) {
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.health = health;
        this.maxHealth = health;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
        this.level = level;
        this.experience = 0;
        this.moves = new ArrayList<>();
        this.isWild = isWild;
        this.statusEffect = "null";
        this.statusDuration = 1;
    }

    public int getBaseSpecialAttack() {
        return specialAttack;
    }

    public int getBaseSpecialDefense() {
        return specialDefense;
    }

    public String getType1() {
        return type1;
    }

    public String getType2() {
        return type2;
    }

    public int getBaseAttack() {
        return attack;
    }

    public int getBaseDefense() {
        return defense;
    }

    public void applyMoveEffect(Pokemon defender, Move move) {
        Random random = new Random();
        if (move.getEffect() != null && random.nextInt(100) < move.getEffectChance()) {
            defender.setStatusEffect(move.getEffect());
            System.out.println(defender.name + " is now affected by " + move.getEffect() + "!");
        }
    }

    public void setStatusEffect(String statusEffect) {
        this.statusEffect = statusEffect;
    }

    public void setStatusDuration(int duration) {
        this.statusDuration = duration;
    }

    public void applyStatusEffects() {
        if (this.statusEffect == null) {
            return;
        }

        switch (this.statusEffect) {
            case "Burn":
                System.out.println(this.name + " is hurt by its burn!");
                this.takeDamage(this.maxHealth / 8);
                break;
            case "Paralysis":
                Random random = new Random();
                if (random.nextInt(100) < 25) {
                    System.out.println(this.name + " is fully paralyzed and can't move!");
                    this.statusEffect = "FP";
                }
                break;
            case "Poison":
                System.out.println(this.name + " is hurt by poison!");
                this.takeDamage(this.maxHealth / 16);
                break;
            case "Confusion":
                if (this.statusDuration > 0) {
                    Random confusionRandom = new Random();
                    if (confusionRandom.nextInt(100) < 50) {
                        System.out.println(this.name + " is confused and hurt itself in its confusion!");
                        this.takeDamage(this.attack / 4);
                        this.statusDuration--;
                    } else {
                        System.out.println(this.name + " snapped out of confusion!");
                        this.statusEffect = null;
                    }
                } else {
                    System.out.println(this.name + " snapped out of confusion!");
                    this.statusEffect = null;
                }
                break;
            case "Sleep":
                if (this.statusDuration > 0) {
                    System.out.println(this.name + " is fast asleep!");
                    this.statusDuration--;
                } else {
                    System.out.println(this.name + " woke up!");
                    this.statusEffect = null;
                }
                break;
                case "Freeze":
                Random freezeRandom = new Random();
                System.out.println(this.name + " is frozen!");
                this.statusDuration = 10;
                if(freezeRandom.nextInt(100) < 15){
                    System.out.println(this.name + " thawed out!");
                }
                break;
        }
    }

    public int attack(Pokemon attacker, Pokemon defender, Move move) {
        Random random = new Random();
        if (random.nextInt(100) >= move.getAccuracy()) {
            System.out.println("The move missed!");
            return 0;
        }
        double typeEffectiveness = TypeEffectiveness.getEffectiveness(move.getType(), defender.getType1(), defender.getType2());
        int attackStat = move.isSpecial() ? attacker.getBaseSpecialAttack() : attacker.getBaseAttack();
        int defenseStat = move.isSpecial() ? defender.getBaseSpecialDefense() : defender.getBaseDefense();

        double damage = ((2 * level / 5 + 2) * move.getPower() * attackStat / defenseStat) / 50 + 2;
        damage *= typeEffectiveness;

        if (typeEffectiveness == 2.0) {
            System.out.println("Attack is super effective!!");
        } else if (typeEffectiveness == 0.5) {
            System.out.println("Attack is not very effective!!");
        } else if (typeEffectiveness == 0.0) {
            System.out.println("Defender is immune to effects!!");
        }
        System.out.println("Damage dealt: " + damage);
        return (int) damage;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
            System.out.println(this.name + " has fainted!");
        }
    }

    public void gainExperience(int exp) {
        this.experience += exp;
        if (this.experience >= 100) { // Example threshold for level up
            levelUp();
        }
    }

    public void levelUp() {
        this.level++;
        this.attack += 5; // Example stat increase
        this.defense += 5;
        this.specialAttack += 5;
        this.specialDefense += 5;
        this.speed += 5;
        this.health += 10;
        this.experience = 0;
        System.out.println(this.name + " leveled up to " + this.level + "!");
    }

    public void learnMove(Move move) {
        this.moves.add(move);
    }

    public void heal(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
        System.out.println(this.name + " healed to " + this.health + " HP!");
    }

    public int getStat(String stat) {
        switch (stat.toLowerCase()) {
            case "health":
                return this.health;
            case "attack":
                return this.attack;
            case "defense":
                return this.defense;
            case "specialattack":
                return this.specialAttack;
            case "specialdefense":
                return this.specialDefense;
            case "speed":
                return this.speed;
            default:
                return 0;
        }
    }

    public static void assignMovesToPokemon(Pokemon pokemon, List<Move> allMoves) {
        List<Move> eligibleMoves = new ArrayList<>();
        for (Move move : allMoves) {
            if (move.getType().equals("Status") ||move.getType().equals("Normal") || move.getType().equals(pokemon.getType1()) || (pokemon.getType2() != null && move.getType().equals(pokemon.getType2()))) {
                eligibleMoves.add(move);
            }
        }
        Random random = new Random();
        while (pokemon.moves.size() < 4 && !eligibleMoves.isEmpty()) {
            Move move = eligibleMoves.remove(random.nextInt(eligibleMoves.size()));
            pokemon.learnMove(move);
        }
    }
}

class Move {

    String name;
    String type;
    String effect;
    int power;
    int accuracy;
    boolean isSpecial;
    int effectChance;

    public Move(String name, String type, String effect, int power, int accuracy, boolean isSpecial, int effectChance) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.effect = effect;
        this.accuracy = accuracy;
        this.isSpecial = isSpecial;
        this.effectChance = effectChance;
    }

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public String getType() {
        return type;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public String getEffect() {
        return effect;
    }

    public int getEffectChance() {
        return effectChance;
    }
}

class Trainer {

    String name;
    List<Pokemon> team;
    List<Item> items;

    public Trainer(String name) {
        this.name = name;
        this.team = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public void catchPokemon(Pokemon wildPokemon) {
        if (wildPokemon.isWild && wildPokemon.health < 150) { // Simplified catching logic
            this.team.add(wildPokemon);
            wildPokemon.isWild = false;
            System.out.println(this.name + " caught " + wildPokemon.name + "!");
        } else {
            System.out.println("Failed to catch " + wildPokemon.name + "!");
        }
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void createTeams(List<Pokemon> pokemons, List<Move> allMoves) {
        Random random = new Random();
        List<Pokemon> team1 = this.team;

        while (team1.size() < 4) {
            Pokemon selectedPokemon = pokemons.get(random.nextInt(pokemons.size()));
            if (!team1.contains(selectedPokemon)) {
                team1.add(selectedPokemon);
                Pokemon.assignMovesToPokemon(selectedPokemon, allMoves);
            }
        }
        System.out.println("Team " + this.name + ":");
        for (Pokemon p : team1) {
            System.out.println(p.name);
        }

    }

    public void useItem(int itemIndex, Pokemon pokemon) {
        if (itemIndex < 0 || itemIndex >= items.size()) {
            System.out.println("Invalid item index!");
            return;
        }
        Item item = items.get(itemIndex);
        item.use(pokemon);
    }

    public void battle(Trainer opponent) {
        Random random = new Random();
        Pokemon myPokemon = this.team.get(random.nextInt(team.size()));
        Pokemon opponentPokemon = opponent.team.get(random.nextInt(team.size()));

        System.out.println(this.name + " sends out " + myPokemon.name + "!");
        System.out.println(opponent.name + " sends out " + opponentPokemon.name + "!");
        int cnt = 1;
        while (myPokemon.health > 0 && opponentPokemon.health > 0) {
            System.out.println("***************************************TURN " + cnt + " ********************************************");
            System.out.println(myPokemon.name + " (" + myPokemon.health + " HP) vs " + opponentPokemon.name + " (" + opponentPokemon.health + " HP)");
            System.out.println("Choose an action: 1) Attack 2) Use Item");

            int myChoice = random.nextInt(2) + 1;
            int opponentChoice = random.nextInt(2) + 1;

            myPokemon.applyStatusEffects();
            opponentPokemon.applyStatusEffects();
            if (myPokemon.health <= 0 && myPokemon.statusEffect.equals("FP") && opponentPokemon.statusEffect.equals("Freeze")){
                break;
            }
                switch (myChoice) {
                    case 1:
                        Move myMove = myPokemon.moves.get(random.nextInt(myPokemon.moves.size()));
                        System.out.println(myPokemon.name + " uses " + myMove.name + "!");
                        opponentPokemon.takeDamage(myPokemon.attack(myPokemon, opponentPokemon, myMove));
                        myPokemon.applyMoveEffect(opponentPokemon, myMove);
                        ;
                        break;
                    case 2:
                        if (!opponent.items.isEmpty()) {
                            opponent.useItem(0, opponentPokemon);
                            opponent.items.remove(0);
                        } else {
                            System.out.println("No items left!");
                        }
                }
            if (opponentPokemon.health <= 0 && opponentPokemon.statusEffect != null && opponentPokemon.statusEffect.equals("FP") && opponentPokemon.statusEffect.equals("Freeze")) {
                break;
            }
                switch (opponentChoice) {
                    case 1:
                        Move opponentMove = opponentPokemon.moves.get(random.nextInt(opponentPokemon.moves.size()));
                        System.out.println(opponentPokemon.name + " uses " + opponentMove.name + "!");
                        myPokemon.takeDamage(opponentPokemon.attack(opponentPokemon, myPokemon, opponentMove));
                        opponentPokemon.applyMoveEffect(myPokemon, opponentMove);
                        break;

                    case 2:
                        if (!this.items.isEmpty()) {
                            this.useItem(0, myPokemon);
                            this.items.remove(0);
                        } else {
                            System.out.println("No items left!");
                        }
                        break;
                }
            System.out.println("*************************************** TURN END ********************************************");
            if (opponentPokemon.statusEffect != null && opponentPokemon.statusEffect.equals("FP") && opponentPokemon.statusEffect.equals("Freeze")) {
                opponentPokemon.statusEffect = null; // Reset the status effect for the next turn
                continue;
            }
            if (myPokemon.statusEffect != null && myPokemon.statusEffect.equals("FP") && opponentPokemon.statusEffect.equals("Freeze")) {
                myPokemon.statusEffect = null; // Reset the status effect for the next turn
                continue;
            }
            cnt++;
            try {
                Thread.sleep(5000); // Pause for a second between moves
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (myPokemon.health > 0) {
            System.out.println(myPokemon.name + " wins the battle!");
            myPokemon.gainExperience(50); // Example experience gain
        } else {
            System.out.println(opponentPokemon.name + " wins the battle!");
            opponentPokemon.gainExperience(50); // Example experience gain
        }
    }
}

class Item {

    String name;
    String effect;
    int potency;

    public Item(String name, String effect, int potency) {
        this.name = name;
        this.effect = effect;
        this.potency = potency;
    }

    public String getName() {
        return name;
    }

    public String getEffect() {
        return effect;
    }

    public int getPotency() {
        return potency;
    }

    public void use(Pokemon pokemon) {
        if (this.effect.equals("heal")) {
            pokemon.heal(this.potency);
        }
    }
}

class TypeEffectiveness {

    private static final double[][] typeChart = {
        //        NOR   FIR   WAT   ELE   GRA   ICE   FIG   POI   GRO   FLY   PSY   BUG   ROC   GHO   DRA   DAR   STE   FAI
        /*NOR*/{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.0, 1.0, 1.0, 0.5, 1.0},
        /*FIR*/ {1.0, 0.5, 0.5, 1.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 0.5, 1.0, 0.5, 1.0, 2.0, 1.0},
        /*WAT*/ {1.0, 2.0, 0.5, 1.0, 0.5, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 2.0, 1.0, 0.5, 1.0, 1.0, 1.0},
        /*ELE*/ {1.0, 1.0, 2.0, 0.5, 0.5, 1.0, 1.0, 1.0, 0.0, 2.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0},
        /*GRA*/ {1.0, 0.5, 2.0, 1.0, 0.5, 1.0, 1.0, 0.5, 2.0, 0.5, 1.0, 0.5, 2.0, 1.0, 0.5, 1.0, 0.5, 1.0},
        /*ICE*/ {1.0, 0.5, 0.5, 1.0, 2.0, 0.5, 1.0, 1.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 0.5, 1.0},
        /*FIG*/ {2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 0.5, 1.0, 0.5, 0.5, 0.5, 2.0, 0.0, 1.0, 2.0, 2.0, 0.5},
        /*POI*/ {1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 0.5, 0.5, 1.0, 1.0, 1.0, 0.5, 0.5, 1.0, 1.0, 0.0, 2.0},
        /*GRO*/ {1.0, 2.0, 1.0, 2.0, 0.5, 1.0, 1.0, 2.0, 1.0, 0.0, 1.0, 0.5, 2.0, 1.0, 1.0, 1.0, 2.0, 1.0},
        /*FLY*/ {1.0, 1.0, 1.0, 0.5, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 0.5, 1.0, 1.0, 1.0, 0.5, 1.0},
        /*PSY*/ {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 0.0, 0.5, 1.0},
        /*BUG*/ {1.0, 0.5, 1.0, 1.0, 2.0, 1.0, 0.5, 0.5, 1.0, 0.5, 2.0, 1.0, 1.0, 0.5, 1.0, 1.0, 0.5, 0.5},
        /*ROC*/ {1.0, 2.0, 1.0, 1.0, 1.0, 2.0, 0.5, 1.0, 0.5, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0},
        /*GHO*/ {0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 2.0, 1.0, 0.5, 1.0, 1.0},
        /*DRA*/ {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 0.5, 0.0},
        /*DAR*/ {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 2.0, 1.0, 0.5, 1.0, 0.5},
        /*STE*/ {1.0, 0.5, 0.5, 0.5, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 0.5, 2.0},
        /*FAI*/ {1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 2.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 0.5, 1.0}
    };

    private static final String[] typeNames = {
        "Normal", "Fire", "Water", "Electric", "Grass", "Ice", "Fighting", "Poison", "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy"
    };

    public static double getEffectiveness(String moveType, String defenderType1, String defenderType2) {
        int moveIndex = getTypeIndex(moveType);
        int defenderIndex1 = getTypeIndex(defenderType1);
        int defenderIndex2 = getTypeIndex(defenderType2);

        double effectiveness = 1.0;
        if (moveIndex != -1 && defenderIndex1 != -1) {
            effectiveness *= typeChart[moveIndex][defenderIndex1];
        }
        if (moveIndex != -1 && defenderIndex2 != -1) {
            effectiveness *= typeChart[moveIndex][defenderIndex2];
        }

        return effectiveness;
    }

    private static int getTypeIndex(String type) {
        for (int i = 0; i < typeNames.length; i++) {
            if (typeNames[i].equalsIgnoreCase(type)) {
                return i;
            }
        }
        return -1;
    }
}

public class Main {

    public static void main(String[] args) {
        List<Pokemon> allPokemons = new ArrayList<>();
        List<Move> allMoves = new ArrayList<>();

        Trainer ash = new Trainer("Ash");
        Trainer misty = new Trainer("Misty");
        Item potion = new Item("HealingPotion", "heal", 10);
        ash.addItem(potion);
        misty.addItem(potion);

        
        allMoves.add(new Move("Tackle", "Normal", null, 40, 100, false, 10));
        allMoves.add(new Move("Ember", "Fire", "Burn", 40, 100, true, 10));
        allMoves.add(new Move("Flamethrower", "Fire", "Burn", 90, 100, true, 10));
        allMoves.add(new Move("Hydro Pump", "Water", null, 110, 80, true, 10));
        allMoves.add(new Move("Solar Beam", "Grass", null, 120, 100, true, 10));
        allMoves.add(new Move("Ice Beam", "Ice", "Freeze", 90, 100, true, 10));
        allMoves.add(new Move("Glare", "Normal", "Paralyze", 0, 100, false, 30));
        allMoves.add(new Move("Poison Powder", "Grass", "Poison", 0, 75, false, 35));
        allMoves.add(new Move("Blizzard", "Ice", "Freeze", 110, 70, true, 10));
        allMoves.add(new Move("Thunderbolt", "Electric", "Paralysis", 90, 100, true, 10));
        allMoves.add(new Move("Psychic", "Psychic", null, 90, 100, true, 10));
        allMoves.add(new Move("Bug Buzz", "Bug", "Poison", 90, 100, true, 10));
        allMoves.add(new Move("Rock Slide", "Rock", null, 75, 90, false, 10));
        allMoves.add(new Move("Hypnosis", "Psychic", "Sleep", 0, 60, false, 20));
        allMoves.add(new Move("Dark Pulse", "Dark",null, 80, 100, true,10));
        allMoves.add(new Move("Lovely Kiss", "Normal", "Sleep", 0, 75, false, 10));

        Pokemon pikachu = new Pokemon("Pikachu", "Electric", null, 40, 55, 40, 50, 50, 90, 5, true);
        Pokemon starmie = new Pokemon("Starmie", "Water", "Psychic", 35, 60, 75, 85, 100, 85, 5, true);
        allPokemons.add(new Pokemon("Bulbasaur", "Grass", "Poison", 30, 49, 49, 65, 65, 45, 5, false));
        allPokemons.add(new Pokemon("Charmander", "Fire", null, 25, 52, 43, 60, 50, 65, 5, false));
        allPokemons.add(new Pokemon("Squirtle", "Water", null, 32, 48, 65, 50, 64, 43, 5, false));
        allPokemons.add(new Pokemon("Scizor", "Bug", "Steel", 33, 130, 100, 55, 80, 65, 5, false));
        allPokemons.add(new Pokemon("Piloswine", "Ice", "Ground", 34, 100, 80, 60, 60, 50, 5, false));
        allPokemons.add(new Pokemon("Hitmontop", "Fighting", null, 27, 95, 95, 35, 110, 70, 5, false));
        allPokemons.add(new Pokemon("Houndoom", "Dark", "Fire", 36, 90, 50, 110, 80, 95, 5, false));

        ash.catchPokemon(pikachu);
        misty.catchPokemon(starmie);

        ash.createTeams(allPokemons, allMoves);
        misty.createTeams(allPokemons, allMoves);

        ash.battle(misty);
    }
}
