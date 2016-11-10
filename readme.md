# Langton's ant

This branch shows a new requirement introduced by Michel Grootjans (https://github.com/michelgrootjans):
   * There is a new green tile, white flips to black, black flips to green, green flips to white
   * The ant turns 180 degrees around
   
Visually represented this looks like:
```kotlin
        test("Turns around when on a green tile, moves forward, leaves it white"){
            var grid = createGrid("""
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    █████←████
                    ██████████
                    ██████████
                    ██████████
                    ██████████""", GREEN)

            grid.tick()

            assertThat(represent(grid), equalTo(represent("""██████████
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    █████□→███
                    ██████████
                    ██████████
                    ██████████
                    ██████████""")))
        }
```

During a socrates antwerp meeting we tdd'd langtons ant (https://en.wikipedia.org/wiki/Langton%27s_ant), code can be found here:
https://github.com/michelgrootjans/langtons-ant

The test code looks like this:

```javascript
  it ('has a grid', function (){
    var grid = new Grid({x: 2, y: 3});
    expect(grid.positionOfAnt()).toEqual({x: 2, y: 3});
    expect(grid.directionOfAnt()).toEqual('north');
  });
```
  
Making assumptions about the implementation like a 2 dimensional grid, but even worse, 
making reasoning about the problem space harder.
 
Therefore I tried to decouple the testing representation from the implementation leading to tests that more or less assert visually:

```kotlin
        test("Turns right when on a black tile, moves forward, leaves it white"){
            var grid = createGrid("""
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    █████←████
                    ██████████
                    ██████████
                    ██████████
                    ██████████""", BLACK)

            grid.tick()

            assertThat(represent(grid), equalTo(represent("""
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    █████↑████
                    █████□████
                    ██████████
                    ██████████
                    ██████████
                    ██████████""")))
        }
```