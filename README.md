# Wayside 
A few years ago I revisited Louis Sachar's *[Sideways Arithmetic From Wayside School](https://archive.org/details/sidewaysarithmet00sach_0)*, a book with logic puzzles 
that while mystifying to me as a Grade 2 student, were now within my comprehension.  Once you get the hang of them, 
the Sideways math problems can be solved on paper in a couple of minutes.

I thought it would be fun to write a program to generate more puzzles, and check my answers.  This project is the result.

Apparently the Wayside books are still in print - consider buying one for the next generation of puzzle solvers!

## How the puzzles work
The first puzzles in *Sideways Arithmetic* take the form of word-based addition: 

```
   ELF
+  ELF
  ----
  FOOL
```

Each letter represents a digit between 0-9, and no two letters can be represented by the same digit.  Can you deduce values for E, L, F, and O that would make the equation work?

<details> 
  <summary>Answer</summary>

   ```
   E = 7
   L = 2
   F = 1
   O = 4
   
      721
   +  721
     ----
     1442
   ```
</details>

Later, Sachar extends the concept to multiplication.  For example:
```
    SAY
x    SI
  -----
   NOSY
+  ICY
  -----
  ANNOY
```

Note that the script can solve both addition and multiplication puzzles, but currently only generates addition puzzles.
A generated puzzle is guaranteed to have a unique solution - that is, each letter has exactly one possible value.

# Usage
- Compile the program: `./build.sh`
  - Requires JDK 17 or later
- Generate and solve puzzles: `./wayside.sh <arguments>`

The puzzle generator uses a dictionary of 5000 common words that you can modify or replace to suit your needs.  The dictionary came from a repo of [public domain word lists](https://github.com/MichaelWehar/Public-Domain-Word-Lists).
## Puzzle generator mode
### Usage 1: Search for puzzles that sum to the given word.

Example:
`./wayside.sh cheddar`

Output:
```
 HYDRATE + DECLARE = CHEDDAR
 CARRIED + SACRED = CHEDDAR
```

### Usage 2: Search for puzzles with the provided addend words.

Example:
`./wayside.sh patrol trolls`

Output:
```
PATROL + TROLLS = SPROUT
```

## Puzzle solver mode

### Usage 1: Solve a puzzle in the form:
```
   CANINE
+  FELINE
  -------
  BALANCE
```

Example:
`./wayside.sh canine feline balance`

Output:
```
A B C E F I L N
5 1 8 0 7 4 6 9
```

### Usage 2: Solve a puzzle in the form:
```
    SAY
x    SI
  -----
   NOSY
+  ICY
  -----
  ANNOY
```

Example:
`./wayside.sh say si nosy icy annoy`

Output:
```
A C I N O S Y
1 4 9 2 8 3 5
```