# Pokemon master name
name = "Ash Ketchum"

# Pokemon Health Points
charmender_HP = 110
squirtle_HP = 125
bulbasaur_HP = 150

# Pokemon Attack Points
charmender_attack = 40
squirtle_attack = 35
bulbasaur_attack = 25

turn = 1

# Turn-based attack loop, default starts with charmender (turn = 1)
# To change default to Squirtle change set 'turn' to 0
while charmender_HP > 0 and squirtle_HP > 0:
    if turn == 1:
        squirtle_HP -= charmender_attack
        print("Charmender did "+str(charmender_attack)+" damage")
        print("Squirtle got hurt :'( HP is: "+str(squirtle_HP))
        turn = 0
    else:
        charmender_HP -= squirtle_attack
        print("Squirtle faught back and did "+str(squirtle_attack)+" damage")
        print("Charmender got bitten! HP is: "+str(squirtle_HP))
        turn = 1

# Print winner pokemon
if charmender_HP >= 1:
    print(name+"'s Charmender won!")
elif squirtle_HP >=1:
    print(name+"'s Squirtle won!")
else:
    print("Something went wrong!!!")

#Find primes in a given interval
begin = 5
end = 25
prime_counter = 0
for num in range(begin, end):
    if(num > 0):
        if(num == 2):
            print("Prime: "+str(num))
        for i in range(2, int(num/2)+2):
            if (num%i==0):
                break
            else:
                print("Prime: "+str(num))
                break


# Some simple equations
eq1 = 2 * -5 + 20
print("EQ1: "+str(eq1))
if(eq1 != 0):
    print("eq1 output not equal to 0")
eq2 = -2 * 3 / 12
print("EQ2: "+str(eq2))

# Uncomment this code block below for BONUS (Syntax error)
# This is just a simple example...
# if(eq2 == 0)
#    print("This code shouldn't run")
# else
#    print "this one as well!"