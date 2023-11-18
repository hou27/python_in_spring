import pickle

def predict(preprocessed_keypoint_3d_str):
    print(preprocessed_keypoint_3d_str)
    preprocessed_keypoint_3d = list(map(float, preprocessed_keypoint_3d_str[1:-1].split(',')))
    print(preprocessed_keypoint_3d)

    with open("./model/ensemble_model.pkl", "rb") as f:
        ensemble_model = pickle.load(f)

        return ensemble_model.predict(preprocessed_keypoint_3d)