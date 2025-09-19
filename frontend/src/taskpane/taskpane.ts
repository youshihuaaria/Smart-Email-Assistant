const API_URL = "https://spu9s5demk.execute-api.us-west-2.amazonaws.com/prod/hello";

Office.onReady((info) => {
  if (info.host === Office.HostType.Outlook) {
    document.getElementById("generate-reply").onclick = generateReply;
  }
})

async function generateReply() {
  const promptInput: HTMLInputElement | null = document.getElementById("prompt") as HTMLInputElement;
  if (!promptInput) {
    console.error("Prompt input not found");
    return;
  }
  const prompt = promptInput.value;

  let mailBody = "";
  try {
    mailBody = await getMailContent(Office.context.mailbox.item);
  } catch (err) {
    mailBody = "(Could not read mail body)";
  }

  document.getElementById("result").innerText = "Calling API... please wait";


}

