import json

import pickle
import math
import numpy as np

from sklearn.preprocessing import MinMaxScaler
from sklearn.ensemble import VotingClassifier

class pushupEnsembleModel:
    def __init__(self):
        # with open(path + "/model/ensemble_model.pkl", "rb") as f:
        with open("./model/ensemble_model.pkl", "rb") as f:
            ensemble_model = pickle.load(f)
        with open("./scaler/scaler1020.pkl", "rb") as f:
            scaler = pickle.load(f)
        self.model = ensemble_model
        self.scaler = scaler

    def predict(self, keypoint_3d):
        preprocessed_data = self.__preprocess(keypoint_3d)
        print(preprocessed_data)
        return self.model.predict(preprocessed_data)

    def __preprocess(self, keypoint_3d):
        if self.scaler is None:
            raise ValueError("Scaler not loaded.")
        data = np.array([np.array([d["x"], d["y"], d["z"]]) for d in keypoint_3d])

        hip_coords = data[23]
        for i in range(data.shape[0]):
            data[i] -= hip_coords


        # Right
        shoulder_right = data[11]
        elbow_right = data[13]
        wrist_right = data[15]
        hip_right = data[23]
        knee_right = data[25]
        ankle_right = data[27]

        # Left
        shoulder_left = data[12]
        elbow_left = data[14]
        wrist_left = data[16]
        hip_left = data[24]
        knee_left = data[26]
        ankle_left = data[28]

        angle_right_arm = self.__calculate_angle(
            shoulder_right, elbow_right, wrist_right
        )
        angle_left_arm = self.__calculate_angle(shoulder_left, elbow_left, wrist_left)
        angle_right_leg = self.__calculate_angle(hip_right, knee_right, ankle_right)
        angle_left_leg = self.__calculate_angle(hip_left, knee_left, ankle_left)

        xy_angle_right_arm = self.__calculate_xy_angle(
            shoulder_right[:-1], elbow_right[:-1], wrist_right[:-1]
        )
        xy_angle_left_arm = self.__calculate_xy_angle(
            shoulder_left[:-1], elbow_left[:-1], wrist_left[:-1]
        )
        xy_angle_right_leg = self.__calculate_xy_angle(
            hip_right[:-1], knee_right[:-1], ankle_right[:-1]
        )
        xy_angle_left_leg = self.__calculate_xy_angle(
            hip_left[:-1], knee_left[:-1], ankle_left[:-1]
        )

        data = np.append(data, round(angle_right_arm, 2))
        data = np.append(data, round(angle_left_arm, 2))
        data = np.append(data, round(angle_right_leg, 2))
        data = np.append(data, round(angle_left_leg, 2))
        data = np.append(data, round(xy_angle_right_arm, 2))
        data = np.append(data, round(xy_angle_left_arm, 2))
        data = np.append(data, round(xy_angle_right_leg, 2))
        data = np.append(data, round(xy_angle_left_leg, 2))

        normalized_data = self.scaler.transform([data])
        preprocessed_data = normalized_data[0][-8:]

        preprocessed_data[[0, 1, 4, 5]] = (
            np.square(preprocessed_data[[0, 1, 4, 5]] * 4) / 2
        )
        preprocessed_data[[2, 3, 6, 7]] = preprocessed_data[[2, 3, 6, 7]] * 4

        return [preprocessed_data]

    def __calculate_angle(self, a, b, c):
        ba = a - b  # vector from point b to a
        bc = c - b  # vector from point b to c

        cosine_angle = np.dot(ba, bc) / (np.linalg.norm(ba) * np.linalg.norm(bc))
        angle = np.arccos(cosine_angle)

        return np.degrees(angle)

    def __calculate_xy_angle(self, a, b, c):
        ba = [a[0] - b[0], a[1] - b[1]]
        bc = [c[0] - b[0], c[1] - b[1]]

        dot_product = ba[0] * bc[0] + ba[1] * bc[1]

        magnitude_ba = math.sqrt(ba[0] ** 2 + ba[1] ** 2)
        magnitude_bc = math.sqrt(bc[0] ** 2 + bc[1] ** 2)

        cos_theta = dot_product / (magnitude_ba * magnitude_bc)

        angle_in_degree = math.acos(cos_theta) * (180 / math.pi)

        return angle_in_degree


class SquatEnsembleModel:
    def __init__(self):
        with open("./model/squart_ensemble_model.pkl", "rb") as f:
            ensemble_model = pickle.load(f)
        with open("./scaler/squart_scaler1020.pkl", "rb") as f:
            scaler = pickle.load(f)
        self.model = ensemble_model
        self.scaler = scaler

    def predict(self, keypoint_3d):
        preprocessed_data = self.__preprocess(keypoint_3d)
        return self.model.predict(preprocessed_data)

    def __preprocess(self, keypoint_3d):
        if self.scaler is None:
            raise ValueError("Scaler not loaded.")
        data = np.array([np.array([d["x"], d["y"], d["z"]]) for d in keypoint_3d])

        hip_coords = data[23]
        for i in range(data.shape[0]):
            data[i] -= hip_coords


        # Right
        hip_right = data[23]
        knee_right = data[25]
        ankle_right = data[27]

        # Left
        hip_left = data[24]
        knee_left = data[26]
        ankle_left = data[28]

        angle_right_leg = self.__calculate_angle(hip_right, knee_right, ankle_right)
        angle_left_leg = self.__calculate_angle(hip_left, knee_left, ankle_left)

        xy_angle_right_leg = self.__calculate_xy_angle(
            hip_right[:-1], knee_right[:-1], ankle_right[:-1]
        )
        xy_angle_left_leg = self.__calculate_xy_angle(
            hip_left[:-1], knee_left[:-1], ankle_left[:-1]
        )

        data = np.append(data, round(angle_right_leg, 2))
        data = np.append(data, round(angle_left_leg, 2))
        data = np.append(data, round(xy_angle_right_leg, 2))
        data = np.append(data, round(xy_angle_left_leg, 2))

        normalized_data = self.scaler.transform([data])
        preprocessed_data = normalized_data[0][-4:]

        preprocessed_data = preprocessed_data * 2

        return [preprocessed_data]

    def __calculate_angle(self, a, b, c):
        ba = a - b  # vector from point b to a
        bc = c - b  # vector from point b to c

        cosine_angle = np.dot(ba, bc) / (np.linalg.norm(ba) * np.linalg.norm(bc))
        angle = np.arccos(cosine_angle)

        return np.degrees(angle)

    def __calculate_xy_angle(self, a, b, c):
        ba = [a[0] - b[0], a[1] - b[1]]
        bc = [c[0] - b[0], c[1] - b[1]]

        dot_product = ba[0] * bc[0] + ba[1] * bc[1]

        magnitude_ba = math.sqrt(ba[0] ** 2 + ba[1] ** 2)
        magnitude_bc = math.sqrt(bc[0] ** 2 + bc[1] ** 2)

        cos_theta = dot_product / (magnitude_ba * magnitude_bc)

        angle_in_degree = math.acos(cos_theta) * (180 / math.pi)

        return angle_in_degree


pushup_model = pushupEnsembleModel()
def execute(data):
    # data = json.loads(data)
    # print(data)
    data = [
        {
            "x": -0.053819116204977036,
            "y": -0.5335278511047363,
            "z": -0.25063684582710266,
            "score": 0.9994493722915649,
            "name": "nose",
        },
        {
            "x": -0.03560296446084976,
            "y": -0.5737643241882324,
            "z": -0.25775232911109924,
            "score": 0.9987234473228455,
            "name": "left_eye_inner",
        },
        {
            "x": -0.03552299365401268,
            "y": -0.5755583643913269,
            "z": -0.25769707560539246,
            "score": 0.998975396156311,
            "name": "left_eye",
        },
        {
            "x": -0.035329703241586685,
            "y": -0.5757691264152527,
            "z": -0.2574390172958374,
            "score": 0.9986988306045532,
            "name": "left_eye_outer",
        },
        {
            "x": -0.06044427305459976,
            "y": -0.5707638263702393,
            "z": -0.2406347692012787,
            "score": 0.9989168643951416,
            "name": "right_eye_inner",
        },
        {
            "x": -0.05997969210147858,
            "y": -0.5712913274765015,
            "z": -0.24305008351802826,
            "score": 0.9991714358329773,
            "name": "right_eye",
        },
        {
            "x": -0.060824327170848846,
            "y": -0.5718116164207458,
            "z": -0.2417135238647461,
            "score": 0.999062716960907,
            "name": "right_eye_outer",
        },
        {
            "x": 0.05527786538004875,
            "y": -0.5803398489952087,
            "z": -0.20849204063415527,
            "score": 0.9990860223770142,
            "name": "left_ear",
        },
        {
            "x": -0.05865207687020302,
            "y": -0.5781159996986389,
            "z": -0.14155231416225433,
            "score": 0.9993451237678528,
            "name": "right_ear",
        },
        {
            "x": -0.01875346526503563,
            "y": -0.5200114846229553,
            "z": -0.23006851971149445,
            "score": 0.9995546340942383,
            "name": "mouth_left",
        },
        {
            "x": -0.051989056169986725,
            "y": -0.5145512819290161,
            "z": -0.20968051254749298,
            "score": 0.9995325803756714,
            "name": "mouth_right",
        },
        {
            "x": 0.1800168752670288,
            "y": -0.4178190231323242,
            "z": -0.1062871590256691,
            "score": 0.9951345324516296,
            "name": "left_shoulder",
        },
        {
            "x": -0.141890749335289,
            "y": -0.4395427405834198,
            "z": -0.09479015320539474,
            "score": 0.9993962645530701,
            "name": "right_shoulder",
        },
        {
            "x": 0.27205345034599304,
            "y": -0.2162456065416336,
            "z": -0.0784779042005539,
            "score": 0.12770254909992218,
            "name": "left_elbow",
        },
        {
            "x": -0.23056916892528534,
            "y": -0.22575607895851135,
            "z": -0.04066173732280731,
            "score": 0.19650980830192566,
            "name": "right_elbow",
        },
        {
            "x": 0.2398059219121933,
            "y": -0.04915419593453407,
            "z": -0.09695281088352203,
            "score": 0.008585016243159771,
            "name": "left_wrist",
        },
        {
            "x": -0.23927980661392212,
            "y": -0.07858394831418991,
            "z": -0.10453706979751587,
            "score": 0.03524139150977135,
            "name": "right_wrist",
        },
        {
            "x": 0.23605358600616455,
            "y": 0.01569535955786705,
            "z": -0.0886107012629509,
            "score": 0.012041179463267326,
            "name": "left_pinky",
        },
        {
            "x": -0.23376724123954773,
            "y": -0.01916261576116085,
            "z": -0.12013287842273712,
            "score": 0.04165929555892944,
            "name": "right_pinky",
        },
        {
            "x": 0.2110181748867035,
            "y": -0.012674044817686081,
            "z": -0.11577658355236053,
            "score": 0.020828597247600555,
            "name": "left_index",
        },
        {
            "x": -0.1902133673429489,
            "y": -0.039940547198057175,
            "z": -0.14155812561511993,
            "score": 0.06632979214191437,
            "name": "right_index",
        },
        {
            "x": 0.22003032267093658,
            "y": -0.0511900819838047,
            "z": -0.09993761032819748,
            "score": 0.02254684641957283,
            "name": "left_thumb",
        },
        {
            "x": -0.21385633945465088,
            "y": -0.07329989969730377,
            "z": -0.12100441008806229,
            "score": 0.06489181518554688,
            "name": "right_thumb",
        },
        {
            "x": 0.1337704360485077,
            "y": 0.014303861185908318,
            "z": 0.0015255078906193376,
            "score": 0.0004318389401305467,
            "name": "left_hip",
        },
        {
            "x": -0.13322922587394714,
            "y": -0.03611418232321739,
            "z": 0.00378307793289423,
            "score": 0.0006214262684807181,
            "name": "right_hip",
        },
        {
            "x": 0.105281300842762,
            "y": 0.015234251506626606,
            "z": -0.11410687118768692,
            "score": 0.00037839566357433796,
            "name": "left_knee",
        },
        {
            "x": -0.04016163572669029,
            "y": -0.28218409419059753,
            "z": -0.18665452301502228,
            "score": 0.00028762980946339667,
            "name": "right_knee",
        },
        {
            "x": 0.17697307467460632,
            "y": 0.3140511214733124,
            "z": -0.0025348274502903223,
            "score": 0.00005724634320358746,
            "name": "left_ankle",
        },
        {
            "x": -0.01883940026164055,
            "y": 0.03015322983264923,
            "z": -0.15828919410705566,
            "score": 0.00001196182165585924,
            "name": "right_ankle",
        },
        {
            "x": 0.16883018612861633,
            "y": 0.3758017122745514,
            "z": 0.03229289501905441,
            "score": 0.00005742647044826299,
            "name": "left_heel",
        },
        {
            "x": -0.04341854527592659,
            "y": 0.0904620885848999,
            "z": -0.08297878503799438,
            "score": 0.00003318589006084949,
            "name": "right_heel",
        },
        {
            "x": 0.2807851731777191,
            "y": 0.05365189164876938,
            "z": -0.31145375967025757,
            "score": 0.000060156518884468824,
            "name": "left_foot_index",
        },
        {
            "x": -0.015509415417909622,
            "y": -0.19010253250598907,
            "z": -0.4606517553329468,
            "score": 0.000049115216825157404,
            "name": "right_foot_index",
        },
    ]
    curr_status = pushup_model.predict(data)[0]
    print(curr_status)
    
    return curr_status



# execute(data)