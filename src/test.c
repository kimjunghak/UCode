void main(){
    int i; int j; int k;
    int rem; int sum;
    int c[10];

    i = test(c, 3);

    while(i <= 500){
        sum = 0;
        k = i/2;
        j = 1;
        while(j<=k){
            rem =i%j;
            if(rem == 0){
                sum = sum + j;
            }
            ++j;
        }
        if(i == sum) write(i);
        ++i;
    }
}

int test(int arr[], int size){
    arr[1] = 2;
    return arr[1];
}
