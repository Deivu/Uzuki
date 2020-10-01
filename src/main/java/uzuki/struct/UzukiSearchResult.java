package uzuki.struct;

public class UzukiSearchResult {
    public final int score;
    public final UzukiShip ship;

    public UzukiSearchResult(int score, UzukiShip ship) {
        this.score = score;
        this.ship = ship;
    }
}
