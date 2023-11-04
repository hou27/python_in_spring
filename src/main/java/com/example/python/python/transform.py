import pickle

def transform(dataArray_str):
    print(dataArray_str)
    dataArray = list(map(float, dataArray_str[1:-1].split(',')))
    print(dataArray)
    with open("src/main/java/com/example/python/python/scaler/scaler1020_protocol2.pkl", "rb") as f:
        scaler = pickle.load(f)

        return scaler.transform(dataArray)