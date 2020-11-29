i = 0
var1 = 4
var2 = 0
var3 = 6
var4 = 0
var5 = 2


while i != 1:
    print('----------------------------------------------------------')
    print('\n')
    valid = False
    if valid:
        print('Not Valid (You should never see this)')

    valid = True

    if valid:
        print('Valid (You should always see this')

    var1 = (var3/350) * 6
    var2 = var1 * 350
    var4 = var1 % var5
    var5 = var1 ^ var5
    var3 = var5 - var1

    print(var1)
    print(var2)
    print(var3)
    print(var4)
    print(var5)

#   Did you find this line?

    if var1 != var2:
        print('Not equal to works')

    if 5 == 5:
        print('equal to works')#What about this one?

    if 5 <= 4:
        print('You should never see this')

    if 4 <= 5:
        print('You should always see this, 4 is less than equal to 5')

    if 4 >= 5:
        print('You should never see this')

    if 5 >= 4:
        #print('This should not be printed, it is a comment')
        print('You should always see this, 5 is greater than equal to 4')

    if 6 > 3:
        print('You should always see this, 6 is greater than 3')

    if 3 > 6:
        print('You should never see this')

    if 3 < 6:
        print('You should always see this, 3 is less than 6')

    var1 += 1
    var2 -= 1
    var3 = 0
    var4 *= 2
    var5 /= 2

    print(var1)
    print(var2)
    print(var3)
    print(var4)
    print(var5)

    var1 ^= 2
    var2 %= 2

    print(var1)
    print(var2)