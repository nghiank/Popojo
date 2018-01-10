using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Net.Sockets;
using System.IO;


public class Login : MonoBehaviour {

	public GameObject email;
	public GameObject password;

	private string emailStr;
	private string passwordStr;

	private TcpClient socket;
	private NetworkStream stream;
	private StreamWriter writer;
	private StreamReader reader;
	private bool socketReady;
	public class NettyData
	{
		public int eventType;
		public string username;
	}


	public void LoginClick() {
		Firebase.Auth.Credential credential =
			Firebase.Auth.EmailAuthProvider.GetCredential(emailStr, passwordStr);
		Firebase.Auth.FirebaseAuth.DefaultInstance.SignInWithCredentialAsync(credential).ContinueWith(task => {
			if (task.IsCanceled) {
				Debug.LogError("SignInWithCredentialAsync was canceled.");
				return;
			}
			if (task.IsFaulted) {
				Debug.LogError("SignInWithCredentialAsync encountered an error: " + task.Exception);
				return;
			}

			Firebase.Auth.FirebaseUser newUser = task.Result;
			Debug.LogFormat("User signed in successfully: {0} ({1})",
				newUser.DisplayName, newUser.UserId);
			Debug.LogFormat("User data:" + newUser.UserId);


			//set sample data
			NettyData data = new NettyData();
			data.eventType = 2;
			data.username = newUser.UserId;
			string tmpString = JsonUtility.ToJson(data);
			Debug.Log("Send this data " + tmpString);


			// TODO: Refactor this into different class
			Debug.Log("Starting to connecting to Netty server...");
			string host = "127.0.0.1";
			int port = 8080;
			socket = new TcpClient(host, port);
			Debug.Log("Setup the socket herer...");
			stream = socket.GetStream();
			writer = new StreamWriter(stream);
			reader = new StreamReader(stream);
			socketReady = true;
			Debug.Log("Setup the socket");

			writer.Write(tmpString);
			writer.Flush();
		});
	}
	// Use this for initialization
	void Start () {
		Screen.orientation = ScreenOrientation.LandscapeLeft;
	}
	
	// Update is called once per frame
	void Update () {
		emailStr = email.GetComponent<InputField> ().text;
		passwordStr = password.GetComponent<InputField> ().text;
		if (socketReady) {
			if (stream.DataAvailable) {
				string data = reader.ReadLine ();
				if (data != null) {

				}
			}
		}
	}

	private void OnIncomingData(string data) {
		Debug.Log ("Server:" + data);
	}

}
