package com.example.Table_Top_Gaming;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Objects;

public class CardGameActivity extends AppCompatActivity {
    private static final String TAG = "CardGameActivity";
    // Create a KEY for passing information to the next activity
    public static final String EXTRA_MESSAGE_CARD = "com.example.load.MESSAGE3";
    private Game game;
    private Gson gson = new Gson();
    private int currentPlayer;
    private int numPlayers;
    private TextView playerNameHeader;
    private ImageButton discardPileButton;
    private ImageButton drawPileButton;
    private RecyclerViewAdapter recyclerViewAdapter;
    private boolean hideHand;

    /**
     * Initialize values that are needed when the game Activity starts.
     * @param savedInstanceState - as required by super(savedInstanceState)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_game);

        // Get the intent for this Activity
        Intent intent = getIntent();

        // Grab the information from GameActivity passed to this Activity and place it in the
        // string message
//        String message = intent.getStringExtra(GameActivity.EXTRA_MESSAGE);
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.d(TAG, "onCreate: Extras is empty!");
        } else {
            Log.d(TAG, "onCreate: Extras has content!");
        }
        String message = Objects.requireNonNull(extras).getString(GameActivity.EXTRA_MESSAGE);
        game = gson.fromJson(message, Game.class);
        currentPlayer = 0;
        Log.d(TAG, "onCreate: Game String: " + message);
        numPlayers = game.getPlayers().size();
        playerNameHeader = findViewById(R.id.playerNameHeader);
        playerNameHeader.setText(game.getPlayers().get(currentPlayer).getName());
        discardPileButton = findViewById(R.id.discardButton);
        if (game.getDiscardPile().size() == 0) {
            discardPileButton.setImageResource(R.drawable.gray_back);
        } else {
            String cardToDiplay = game.getDiscardPile().get(game.getDiscardPile().size() - 1).getSuit().toString() +
                    game.getDiscardPile().get(game.getDiscardPile().size() - 1).getNumber();
            int id = this.getResources().getIdentifier(cardToDiplay, "drawable", this.getPackageName());
            discardPileButton.setImageResource(id);
        }
        drawPileButton = findViewById(R.id.deckButton);

        hideHand = true;

        initRecyclerView();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationMenu);
        bottomNavigationView.setSelectedItemId(R.id.navigation_cards);
    }

    public void shuffleDeck() {
        game.getDeck().shuffle();
    }

    public void putDiscardToDeck() {
        while (game.getDiscardPile().size() > 0) {
            game.getDeck().getDeck().add(game.getDiscardPile().get(0));
            game.getDiscardPile().remove(0);
        }
    }

    public void shuffleClicked(View view) {
        putDiscardToDeck();
        shuffleDeck();
        updateImagesForCardLocations();
    }

    public void updateImagesForCardLocations() {
        if (game.getDiscardPile().size() > 0) {
            String cardToDiplay = game.getDiscardPile().get(game.getDiscardPile().size() - 1).getSuit().toString() +
                    game.getDiscardPile().get(game.getDiscardPile().size() - 1).getNumber();
            int id = this.getResources().getIdentifier(cardToDiplay, "drawable", this.getPackageName());
            discardPileButton.setImageResource(id);
        } else {
            discardPileButton.setImageResource(R.drawable.gray_back);
        }

        if (game.getDeck().getDeck().size() == 0) {
            drawPileButton.setImageResource(R.drawable.gray_back);
        } else {
            drawPileButton.setImageResource(R.drawable.red_back);
        }

        recyclerViewAdapter.notifyDataSetChangedWithExtras(hideHand, game.getPlayers().get(currentPlayer).getHand());

        playerNameHeader.setText(game.getPlayers().get(currentPlayer).getName());

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: <- What that says.");
        RecyclerView recyclerView = findViewById(R.id.handRecyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(game.getPlayers().get(currentPlayer).getHand(), game.getDiscardPile(), hideHand, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void drawClicked(View view) {
        if (game.getDeck().getDeck().size() > 0) {
            game.getPlayers().get(currentPlayer).getHand().add(game.getDeck().drawCard());
            recyclerViewAdapter.notifyDataSetChanged();
            updateImagesForCardLocations();
        } else {
            Toast.makeText(this, "The Draw Pile Is Empty.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void discardClicked(View view) {
        if(game.getDiscardPile().size() == 0) {
            Toast.makeText(this, "The Discard Pile Is Empty.",
                    Toast.LENGTH_LONG).show();
            discardPileButton.setImageResource(R.drawable.gray_back);
        } else {
            game.getPlayers().get(currentPlayer).getHand().add(game.getDiscardPile().get(game.getDiscardPile().size() - 1));
            game.getDiscardPile().remove(game.getDiscardPile().size() - 1);
            recyclerViewAdapter.notifyDataSetChanged();

            if (game.getDiscardPile().size() == 0) {
                discardPileButton.setImageResource(R.drawable.gray_back);
            } else {
                String cardToDiplay = game.getDiscardPile().get(game.getDiscardPile().size() - 1).getSuit().toString() +
                        game.getDiscardPile().get(game.getDiscardPile().size() - 1).getNumber();
                int id = this.getResources().getIdentifier(cardToDiplay, "drawable", this.getPackageName());
                discardPileButton.setImageResource(id);
            }
        }
    }

    public void hideClicked(View view) {
        hideHand = !hideHand;
        updateImagesForCardLocations();
    }

    public void prevPlayerClicked(View view) {
        if (currentPlayer > 0) {
            currentPlayer--;
        }
        else {
            currentPlayer = numPlayers - 1;
        }
        hideHand = true;
        CheckBox checkBox = findViewById(R.id.hideCheck);
        checkBox.setChecked(true);
        updateImagesForCardLocations();
    }

    public void nextPlayerClicked(View view) {
        if (currentPlayer < numPlayers) {
            currentPlayer++;
        }
        if (currentPlayer >= numPlayers) {
            currentPlayer = 0;
        }
        hideHand = true;
        CheckBox checkBox = findViewById(R.id.hideCheck);
        checkBox.setChecked(true);
        updateImagesForCardLocations();
    }

    /**
     * This function calls a dialog box that lets a user roll different kinds of dice and displays
     * the results on the screen
     * @param menuItem this is the MenuItem that calls this function
     */
    public void diceClicked(MenuItem menuItem) {
        // Create a new dieRoller that will keep track of all the dice
        final DieRoller dieRoller = new DieRoller();

        // Create a new Alert Dialog and set the view to the dice rolling custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(CardGameActivity.this);
        View view = getLayoutInflater().inflate(R.layout.activity_roll_dice, null);

        // Create variables for the different text fields on the dice rolling custom layout
        final TextView toBeRolled = (TextView) view.findViewById(R.id.diceBeingRolled);
        final TextView total = (TextView) view.findViewById(R.id.sumOfDice);

        // Create buttons for all the different buttons on the dice rolling custom layout
        Button zero = (Button) view.findViewById(R.id.zero);
        Button one = (Button) view.findViewById(R.id.one);
        Button two = (Button) view.findViewById(R.id.two);
        Button three = (Button) view.findViewById(R.id.three);
        Button four = (Button) view.findViewById(R.id.four);
        Button five = (Button) view.findViewById(R.id.five);
        Button six = (Button) view.findViewById(R.id.six);
        Button seven = (Button) view.findViewById(R.id.seven);
        Button eight = (Button) view.findViewById(R.id.eight);
        Button nine = (Button) view.findViewById(R.id.nine);
        Button delete = (Button) view.findViewById(R.id.delete);
        Button plus = (Button) view.findViewById(R.id.roll_plus);
        Button roll = (Button) view.findViewById(R.id.roll);
        Button d4 = (Button) view.findViewById(R.id.d4);
        Button d6 = (Button) view.findViewById(R.id.d6);
        Button d8 = (Button) view.findViewById(R.id.d8);
        Button d10 = (Button) view.findViewById(R.id.d10);
        Button d20 = (Button) view.findViewById(R.id.d20);

        // The zero button has been pressed check for foolish user input
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "0");
            }
        });

        // The one button has been pressed check for foolish user input
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "1");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "1");
            }
        });

        // The two button has been pressed check for foolish user input
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "2");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "2");
            }
        });

        // The three button has been pressed check for foolish user input
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "3");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "3");
            }
        });

        // The four button has been pressed check for foolish user input
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "4");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "4");
            }
        });

        // The five button has been pressed check for foolish user input
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "5");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "5");
            }
        });

        // The six button has been pressed check for foolish user input
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "6");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "6");
            }
        });

        // The seven button has been pressed check for foolish user input
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "7");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "7");
            }
        });

        // The eight button has been pressed check for foolish user input
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "8");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "8");
            }
        });

        // The nine button has been pressed check for foolish user input
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    toBeRolled.setText(toBeRolled.getText().toString() + "9");
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                toBeRolled.setText(toBeRolled.getText().toString() + "9");
            }
        });

        // The D4 button has been pressed check for foolish user input and a some D4 to the dieRoller
        d4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }
                else {
                    int startIndex = 0;
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    int numDice;
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex, toBeRolled.getText().toString().length()));
                    }
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    toBeRolled.setText(toBeRolled.getText().toString() + "d4");
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(4));
                    }
                    Log.i("e", "DIEROLLER SIZE: " + dieRoller.getDice().size());
                }
            }
        });

        // The D6 button has been pressed check for foolish user input and a some D6 to the dieRoller
        d6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }
                else {
                    int startIndex = 0;
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    int numDice;
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex, toBeRolled.getText().toString().length()));
                    }
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    toBeRolled.setText(toBeRolled.getText().toString() + "d6");
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(6));
                    }
                    Log.i("e", "DIEROLLER SIZE: " + dieRoller.getDice().size());
                }
            }
        });

        // The D8 button has been pressed check for foolish user input and a some D8 to the dieRoller
        d8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }
                else {
                    int startIndex = 0;
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    int numDice;
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex, toBeRolled.getText().toString().length()));
                    }
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    toBeRolled.setText(toBeRolled.getText().toString() + "d8");
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(8));
                    }
                    Log.i("e", "DIEROLLER SIZE: " + dieRoller.getDice().size());
                }
            }
        });

        // The D10 button has been pressed check for foolish user input and a some D10 to the dieRoller
        d10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }
                else {
                    int startIndex = 0;
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    int numDice;
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex, toBeRolled.getText().toString().length()));
                    }
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    toBeRolled.setText(toBeRolled.getText().toString() + "d10");
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(10));
                    }
                    Log.i("e", "DIEROLLER SIZE: " + dieRoller.getDice().size());
                }
            }
        });

        // The D20 button has been pressed check for foolish user input and a some D20 to the dieRoller
        d20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            return;
                        }
                    }
                }

                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }
                else {
                    int startIndex = 0;
                    for (int i = toBeRolled.getText().toString().length() - 1; i > -1; i--) {
                        if (toBeRolled.getText().toString().charAt(i) == ' ') {
                            startIndex = i + 1;
                            break;
                        }
                    }

                    int numDice;
                    if (toBeRolled.getText().toString().length() > 1) {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(startIndex, toBeRolled.getText().toString().length()));
                    }
                    else {
                        numDice = Integer.parseInt(toBeRolled.getText().toString().substring(toBeRolled.getText().toString().length() - 1));
                    }

                    toBeRolled.setText(toBeRolled.getText().toString() + "d20");
                    for (int i = 0; i < numDice; i++) {
                        dieRoller.addDie(new Die(20));
                    }
                    Log.i("e", "DIEROLLER SIZE: " + dieRoller.getDice().size());
                }
            }
        });

        // The delete button has been pressed, delete the text on the screen and remove dice from the diceRoller if necessary
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {

                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i).getNumSides() == 4) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    // This if statement now compares the current die to a "d4".
                                    if (dieRoller.getDice().get(0).getNumSides() == 4 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {

                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i).getNumSides() == 6) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    if (dieRoller.getDice().get(0).getNumSides() == 6 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8'
                            && toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -2));
                        for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                            if (dieRoller.getDice().get(i).getNumSides() == 8) {
                                dieRoller.getDice().remove(i);
                                i = 0;
                                if (dieRoller.getDice().size() > 0) {
                                    if (dieRoller.getDice().get(0).getNumSides() == 8 && dieRoller.getDice().size() == 1) {
                                        dieRoller.getDice().clear();
                                    }
                                }
                            }
                        }
                        return;
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == '1') {
                                toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                                for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                                    if (dieRoller.getDice().get(i).getNumSides() == 10) {
                                        dieRoller.getDice().remove(i);
                                        i = 0;
                                        if (dieRoller.getDice().size() > 0) {
                                            if (dieRoller.getDice().get(0).getNumSides() == 10 && dieRoller.getDice().size() == 1) {
                                                dieRoller.getDice().clear();
                                            }
                                        }
                                    }
                                }
                                return;
                            }

                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == '2') {
                                toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                                for (int i = 0; i < dieRoller.getDice().size(); i ++) {
                                    if (dieRoller.getDice().get(i).getNumSides() == 20) {
                                        dieRoller.getDice().remove(i);
                                        i = 0;
                                        if (dieRoller.getDice().size() > 0) {
                                            if (dieRoller.getDice().get(0).getNumSides() == 20 && dieRoller.getDice().size() == 1) {
                                                dieRoller.getDice().clear();
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                        toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -3));
                        return;
                    }
                }
                if (toBeRolled.getText().toString().length() > 0) {
                    toBeRolled.setText(toBeRolled.getText().toString().substring(0, toBeRolled.getText().toString().length() -1));
                    return;
                }
            }
        });

        // The plus button has been press just display a plus on the screen and check for foolish user input
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().equals("") || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) == ' ') {
                    return;
                }

                if (toBeRolled.getText().toString().length() > 1) {
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                            || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                            toBeRolled.setText(toBeRolled.getText().toString() + " + ");
                            return;
                        }
                    }
                    if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                            && toBeRolled.getText().toString().length() > 2) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                            toBeRolled.setText(toBeRolled.getText().toString() + " + ");
                            return;
                        }
                    }
                }
            }
        });

        // The roll button has been pressed roll all the dice and display the results to the screen and check for foolish user input
        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeRolled.getText().toString().isEmpty()) {
                    return;
                }
                if (!dieRoller.getDice().isEmpty() || toBeRolled.getText().toString().charAt((toBeRolled.getText().toString().length() - 1)) != ' ') {

                    if (toBeRolled.getText().toString().length() > 1) {
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '4'
                                || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '6'
                                || toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '8') {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 2) == 'd') {
                                dieRoller.rollAllDice();
                                total.setText(dieRoller.display());
                                dieRoller.getDice().clear();
                                toBeRolled.setText("");
                                return;
                            }
                        }
                        if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 1) == '0'
                                && toBeRolled.getText().toString().length() > 2) {
                            if (toBeRolled.getText().toString().charAt(toBeRolled.getText().toString().length() - 3) == 'd') {
                                dieRoller.rollAllDice();
                                total.setText(dieRoller.display());
                                dieRoller.getDice().clear();
                                toBeRolled.setText("");
                                return;
                            }
                        }
                    }
                }
            }
        });

        builder.setView(view)
                // Add a OK button to the dialog
                .setPositiveButton(R.string.player_score_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user hit "OK" do nothing they are done
                    }
                }).setNegativeButton(R.string.player_score_cancel, new DialogInterface.OnClickListener() {
            // Add a CANCEL button to the dialog
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The user hit "CANCEL" do nothing they are done
            }
        });

        // Create and show the dialog to the screen
        AlertDialog dialog = builder.create();
        dialog.show();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationMenu);
        bottomNavigationView.setSelectedItemId(R.id.navigation_cards);
    }

    public void gridClicked(MenuItem menuItem) {

    }

    public void saveClicked(MenuItem menuItem) {

    }

    public void homeClicked(MenuItem menuItem) {
        assert game != null;
        String gameInformation = gson.toJson(game);
        Log.d(TAG, "returnToScore: Game Info:" + gameInformation);
        Intent intent = new Intent(CardGameActivity.this, GameActivity.class);
        intent.putExtra(EXTRA_MESSAGE_CARD, gameInformation);
        Log.d(TAG, "returnToScore: ExtraMessageInIntent: " + intent.getStringExtra(EXTRA_MESSAGE_CARD));
        startActivity(intent);
    }

    public void cardClicked(MenuItem menuItem) {

    }
}
