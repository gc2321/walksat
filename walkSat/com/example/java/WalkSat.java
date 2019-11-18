package walkSat.com.example.java;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class WalkSat {

    Random rand;
    List<List<Integer>> clauses;
    Map<Integer, Boolean> map;
    List<Integer> symbols;
    Set<Integer> symbolSet;
    int totalFlipUsed;

    public WalkSat(int clauseNum, int symNum) {
        rand = new Random();
        totalFlipUsed = 0;
        this.symbols = new ArrayList<Integer>();
        this.clauses = new ArrayList<List<Integer>>();
        this.map = new HashMap<Integer, Boolean>();
        this.symbolSet = new HashSet<Integer>();

        for (int i = 1; i <= symNum; i++) {
            this.symbols.add(i);
        }

        // generate clauses
        for (int i = 0; i < clauseNum; i++) {
            List<Integer> tempSymb = new ArrayList<>(symbols);
            List<Integer> clause = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                int randIndex = rand.nextInt(tempSymb.size());
                int num = tempSymb.remove(randIndex);
                int randSign = rand.nextInt(2);
                // clause.add(num);
                if (randSign % 2 == 0)
                    clause.add(num);
                else
                    clause.add(-1 * (num));
                this.symbolSet.add(num);
            }
            this.clauses.add(clause);
        }

        // generate map
        for (Integer i : this.symbols) {
            // this.map.put(i, false);
            int randInt = rand.nextInt(2);
            if (randInt % 2 == 0) {
                this.map.put(i, true);
            } else {
                this.map.put(i, false);
            }
        }
    }

    public static void main(String[] args) {

        try {
            PrintWriter write = new PrintWriter(new File("test.csv"));
            StringBuilder sb = new StringBuilder();
            sb.append("clause,");
            sb.append(',');
            sb.append("times");
            sb.append('\n');

            for (int c = 20; c <= 200; c += 20) {
                for (int it = 0; it < 50; it++) {
                    boolean solved = false;
    
                    WalkSat ws = new WalkSat(c, 20);
                    for (int i = 0; i < ws.clauses.size(); i++) {
                        // for (int j = 0; j < 3; j++) {
                        // System.out.println(ws.clauses.get(i).get(j));
                        // }
                        // System.out.println();
                    }
    
                    long start = System.currentTimeMillis();
    
                    // while(ws.totalFlipUsed <= ws.maxFlips) {
                    while (true) {
                        List<List<Integer>> listOfFalseClauses = ws.getAllFalseClauses();
                        if (listOfFalseClauses.size() == 0) {
                            solved = true;
                            break;
                        } else {
                            int randNum = ws.rand.nextInt(10);
                            if (randNum <= 4) {
                                List<Integer> randomFalseClause = ws.getRandomClause(listOfFalseClauses);
                                ws.randomSymbolFlip(randomFalseClause);
                            } else {
                                int symb = ws.getSymbolWithMinFalseClauses();
                                ws.flipSymbol(symb);
                            }
                        }
    
                        long end = System.currentTimeMillis();
                        float sec = (end - start) / 1000F;
                        if (sec > 10) {
                            break;
                        }
                    }
    
                    if (solved) {
                        System.out.println(it + " clause " + c + " used " + ws.totalFlipUsed);
                        sb.append(String.valueOf(c));
                        sb.append(',');
                        sb.append(String.valueOf(ws.totalFlipUsed));
                        sb.append('\n');
                        // for (Integer key : ws.map.keySet()) {
                        // System.out.println(key + " " + ws.map.get(key));
                        // }
                    } else {
                        System.out.println(it + " clause " + c + " time out");
                        sb.append(String.valueOf(c));
                        sb.append(',');
                        sb.append(String.valueOf(-1));
                        sb.append('\n');
                    }
                }
            }
    
            write.write(sb.toString());
            write.close();

        } catch (FileNotFoundException e) {

        }

        
    }

    public List<List<Integer>> getAllFalseClauses() {
        List<List<Integer>> falseClauses = new ArrayList<>();
        for (List<Integer> clause : this.clauses) {
            int flag = 0;
            for (Integer symbol : clause) {
                if (!isSymbolTrue(symbol)) {
                    flag++;
                }
            }
            if (flag == 3) {
                falseClauses.add(clause);
            }
        }
        return falseClauses;
    }

    public List<List<Integer>> getAllFalseClauses(int flipSym) {
        List<List<Integer>> falseClauses = new ArrayList<>();
        for (List<Integer> clause : this.clauses) {
            boolean flag = true;
            for (Integer symbol : clause) {
                if (!isSymbolTrue(symbol)) {
                    flag = symbol == flipSym;
                } else {
                    flag = symbol != flipSym;
                }
                if (!flag) {
                    falseClauses.add(clause);
                }
            }
        }
        return falseClauses;
    }

    public int getSymbolWithMinFalseClauses() {
        int symbol = -1;
        int minFalseClauses = Integer.MAX_VALUE;

        for (Integer symb : this.map.keySet()) {
            if (!this.symbolSet.contains(symb))
                continue;
            int falseClause = this.getAllFalseClauses(symb).size();
            if (falseClause < minFalseClauses) {
                symbol = symb;
                minFalseClauses = falseClause;
            }
        }
        return symbol;
    }

    public List<Integer> getRandomClause(List<List<Integer>> list) {
        int randIndex = rand.nextInt(list.size());
        return list.get(randIndex);
    }

    public int checkTotalFalseClause() {
        int count = 0;
        for (List<Integer> clause : clauses) {
            if (getFalseInClause(clause) > 0) {
                count++;
            }
        }
        return count;
    }

    public int getFalseInClause(List<Integer> clause) {
        int falseCount = 0;
        for (Integer symb : clause) {
            if (!isSymbolTrue(symb))
                falseCount++;
        }
        return falseCount;
    }

    public boolean isSymbolTrue(int symb) {
        if (this.map.get(Math.abs(symb)) == true) {
            return symb > 0;
        }
        return symb < 0;
    }

    public void randomSymbolFlip(List<Integer> clause) {
        List<Integer> listOfFalseSymbols = new ArrayList<>();
        for (int symbol : clause) {
            if (!isSymbolTrue(symbol)) {
                listOfFalseSymbols.add(symbol);
            }
        }
        int randomSymbolIndex = rand.nextInt(listOfFalseSymbols.size());
        int symbol = listOfFalseSymbols.get(randomSymbolIndex);
        this.flipSymbol(symbol);
    }

    public void flipSymbol(int symbol) {
        this.map.put(Math.abs(symbol), !this.map.get(Math.abs(symbol)));
        this.totalFlipUsed++;
    }
}